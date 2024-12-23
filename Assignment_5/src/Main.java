import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;

//  Static Class
class DataManager {
    public static Vector<Integer> scoreList = new Vector<>();

    public static void addScore(int score) {
        scoreList.add(score);
    }

    public static int getMaxScore() {
        int maxScore = 0;
        for (var e : scoreList) {
            if (e > maxScore) {
                maxScore = e;
            }
        }

        return maxScore;
    }

    public static int getLastScore() {
        if (scoreList.isEmpty()) {
            return 0;
        }
        return scoreList.getLast();
    }
}

//  Singleton
class GameManager {
    int ballCount;
    //  0 : startScene, 1 : GameScene, 2 : GameOver
    int sceneNumber;

    int blockNum;
    int score;
    int level;

    boolean isInGame;

    Main frame;

    private static GameManager instance;

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    GameManager() {
        ballCount = 0;
        sceneNumber = 0;
        score = 0;

        isInGame = false;

        level = 1;
    }

    public boolean getInGame() {
        return isInGame;
    }

    public void setInGame(boolean inGame) {
        isInGame = inGame;
    }

    public boolean gameOver() {
        if (isInGame) {
            if (ballCount <= 0) {
                setInGame(false);
                level = 1;
                DataManager.addScore(score);
                score = 0;
                return true;
            }
        }
        return false;
    }

    public boolean stageClear() {
        if (isInGame) {
            if (blockNum <= 0) {
                setInGame(false);
                level++;
                return true;
            }
        }
        return false;
    }

    public void setFrame(Main frame) {
        this.frame = frame;
    }

    public Main getFrame() {
        return frame;
    }
}

//  Singleton
class AudioManager {
    private static AudioManager instance;

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public enum musicList {
        Opening,
        InGame,
        Hit,
        Duplicate,
        GameOver
    }

    public Clip[] clips;
    public URL[] files;
    public AudioInputStream[] audioStreams;

    public AudioManager() {
        clips = new Clip[musicList.values().length];
        files = new URL[musicList.values().length];
        audioStreams = new AudioInputStream[musicList.values().length];

        // 리소스 경로로 변경
        files[0] = getClass().getClassLoader().getResource("Opening.wav");
        files[1] = getClass().getClassLoader().getResource("InGame.wav");
        files[2] = getClass().getClassLoader().getResource("Hit.wav");
        files[3] = getClass().getClassLoader().getResource("Duplicate.wav");
        files[4] = getClass().getClassLoader().getResource("GameOver.wav");

        for (int i = 0; i < musicList.values().length; i++) {
            try {
                audioStreams[i] = AudioSystem.getAudioInputStream(files[i]);
                clips[i] = AudioSystem.getClip();
                clips[i].open(audioStreams[i]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startBGM(musicList track) {
        for (Clip clip : clips) {
            clip.stop();
        }

        int tmp = track.ordinal();
        clips[tmp].setFramePosition(0); // 위치 초기화
        clips[tmp].start();

        if (track != musicList.GameOver) {
            clips[tmp].loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void startSFX(musicList track) {
        for (Clip clip : clips) {
            if (!clip.isRunning()) {
                clip.setFramePosition(0);
            }
        }
        int tmp = track.ordinal();
        clips[tmp].start();
    }
}

class GameOverScene extends JPanel implements Runnable {

    boolean spaceTextVisible;

    Vector<FallSnow> snows = new Vector<>();
    Vector<FallSnow> toAddSnow = new Vector<>();

    GameOverScene() {
        setFocusable(true);
        requestFocus();

        spaceTextVisible = true;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    GameManager.getInstance().sceneNumber = 0;
                    GameManager.getInstance().getFrame().setScene();
                }
            }
        });

        Thread th = new Thread(this);
        th.start();

        AudioManager.getInstance().startBGM(AudioManager.musicList.GameOver);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(10, 12, 32), 0, getHeight(), new Color(74, 114, 177));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        //  Snow Effect
        snows.addAll(toAddSnow);
        toAddSnow.clear();
        if(toAddSnow.isEmpty()) {
            var it = snows.iterator();
            while (it.hasNext()) {
                it.next().draw(g2);
            }
        }

        //  그림자
        g2.setColor(new Color(218, 165, 32));
        g2.setFont(new Font("Georgia", Font.BOLD, 90));
        g2.drawString("Game Over", 140 + 2, 230 + 2);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Game Over", 140, 230);

        g2.setFont(new Font("Georgia", Font.BOLD, 40));
        g2.setColor(new Color(218, 165, 32));
        g2.drawString("Best Score : " + DataManager.getMaxScore(), 260, 410);
        g2.setColor(Color.WHITE);
        g2.drawString("Your Score : " + DataManager.getLastScore(), 250, 470);

        if (spaceTextVisible) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.drawString("Press Space bar to Retry!", 220 + 2, 600 + 2);
            g2.setColor(Color.WHITE);
            g2.drawString("Press Space bar to Retry!", 220, 600);
        }
    }

    @Override
    public void run() {
        int elapsed = 0;
        int snowElapsed = 0;

        while (true) {
            try {
                repaint();

                elapsed += 10;
                snowElapsed += 10;

                if (elapsed > 700 && elapsed < 1000) {
                    spaceTextVisible = false;
                }
                else if (elapsed >= 1000) {
                    spaceTextVisible = true;
                    elapsed = 0;
                }

                //  Update Snow
                var it = snows.iterator();
                while (it.hasNext()) {
                    var tmp = it.next();
                    tmp.update(tmp.deltaT);

                    if(tmp.isFallDown()){
                        it.remove();
                    }
                }

                //  Add Snow
                if (snowElapsed > 500) {
                    snowElapsed = 0;
                    for (int i = 0; i < 20; i++) {
                        toAddSnow.add(new FallSnow());
                    }
                }

                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


class StartScene extends JPanel implements Runnable {

    Vector<FlickeringStar> stars = new Vector<>();

    int starNum = 200;

    boolean spaceTextVisible;

    StartScene() {
        setFocusable(true);
        setLayout(null);

        spaceTextVisible = true;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    GameManager.getInstance().sceneNumber = 1;
                    GameManager.getInstance().getFrame().setScene();
                    System.out.println("Scene Number : " + GameManager.getInstance().sceneNumber);
                }
            }
        });

        for (int i = 0; i < starNum; i++) {
            stars.add(new FlickeringStar());
        }

        Thread th = new Thread(this);
        th.start();

        AudioManager.getInstance().startBGM(AudioManager.musicList.Opening);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, 0, getHeight(), new Color(30, 30, 30));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (var e : stars) {
            e.draw(g2);
        }

        //  그림자
        g2.setColor(new Color(218, 165, 32));
        g2.setFont(new Font("Georgia", Font.BOLD, 40));
        g2.drawString("Java Programming", 205 + 2, 180 + 2);
        g2.drawString("Assignment #5", 250 + 2, 240 + 2);
        g2.setFont(new Font("Georgia", Font.BOLD, 90));
        g2.drawString("Block Breaker", 80 + 2, 400 + 2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Georgia", Font.BOLD, 40));
        g2.drawString("Java Programming", 205, 180);
        g2.drawString("Assignment #5", 250, 240);

        g2.setFont(new Font("Georgia", Font.BOLD, 90));
        g2.drawString("Block Breaker", 80, 400);

        if (spaceTextVisible) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 30));
            g2.drawString("Press Space bar to Start!", 220 + 2, 600 + 2);
            g2.setColor(Color.WHITE);
            g2.drawString("Press Space bar to Start!", 220, 600);
        }

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("21011746 Yang HyunSeok", 645, 757);
    }

    @Override
    public void run() {
        int elapsed = 0;
        while (true) {
            try {
                repaint();
                Thread.sleep(10);
                elapsed += 10;

                if (elapsed > 700 && elapsed < 1000) {
                    spaceTextVisible = false;
                }
                else if (elapsed >= 1000) {
                    spaceTextVisible = true;
                    elapsed = 0;
                }
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class BlockPanel extends JPanel {
    Block[][] blocks;

    float width;
    float height;

    static final float padding = 5f;

    int num;

    BlockPanel() {
        setSize(800 - Wall.offset * 2, 350);
        setOpaque(false);
        setBackground(null);

        num = 2 * GameManager.getInstance().level + 1;

        //  Padding을 제외한 길이
        width = getWidth() - padding * (num + 1);
        height = getHeight() - padding * (num + 1);

        //  각 칸의 크기
        float dx = width / num;
        float dy = height / num;

        blocks = new Block[num][];
        for (int i = 0; i < num; i++) {
            blocks[i] = new Block[num];
        }

        //  Starting Point
        float sx;
        float sy = Wall.offset + padding;

        for (int i = 0; i < num; i++) {
            sx = Wall.offset + padding;

            for (int j = 0; j < num; j++) {
                blocks[i][j] = new Block();
                blocks[i][j].setBlock(sx, sy, dx, dy);
                GameManager.getInstance().frame.gameScene.objects.add(blocks[i][j]);

                sx += (dx + padding);
            }
            sy += (dy + padding);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                blocks[i][j].draw(g);
            }
        }
    }
}

class GameScene extends JPanel implements Runnable {
    Vector<GameObject> objects = new Vector<>();

    //  Iterator traversal에서 생기는 Exception 방지
    LinkedList<GameObject> toAddObjects = new LinkedList<>();

    Player player;

    BlockPanel blockPanel;

    boolean isClear;
    boolean clearTextVisible;

    GameScene() {
        AudioManager.getInstance().startBGM(AudioManager.musicList.InGame);

        objects.add(new Wall(0, 0, 800, Wall.offset, true));
        objects.add(new Wall(0, Wall.offset, Wall.offset, 800, false));
        objects.add(new Wall(800 - Wall.offset, Wall.offset, Wall.offset, 800, false));

        player = new Player();
        objects.add(player);

        setFocusable(true);
        requestFocus();

        //  가독성을 위해 KeyListener에서 KeyAdapter로 변경
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                player.move(e.getKeyCode());
            }

            //  키가 놓아졌을 때
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    player.isLeft = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    player.isRight = false;
                }
            }
        });

        //  Thread Start
        Thread t = new Thread(this);
        t.start();
    }

    public void initGame() {
        //  Block Panel
        blockPanel = new BlockPanel();
        blockPanel.setLocation((int) BlockPanel.padding, (int) BlockPanel.padding);
        add(blockPanel);

        reset();

        //  Player
        player.setOrigin();

        //  Setting
        GameManager.getInstance().ballCount = 1;
        GameManager.getInstance().setInGame(true);
        GameManager.getInstance().blockNum = blockPanel.num * blockPanel.num;
        isClear = false;

        //  First Ball
        toAddObjects.add(new Ball());
    }

    public void reset() {
        var it = objects.iterator();
        while (it.hasNext()) {
            var tmp = it.next();

            if (tmp instanceof Ball) {
                it.remove();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(224, 240, 248), 0, getHeight(), new Color(146, 180, 235));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        //  충돌 방지 용
        var it = objects.iterator();
        while (it.hasNext()) {
            it.next().draw(g);
        }

        //  Clear Text
        if (isClear && clearTextVisible) {
            g2.setColor(new Color(218, 165, 32));
            g2.setFont(new Font("Georgia", Font.BOLD, 80));
            g2.drawString("Stage " + (GameManager.getInstance().level - 1) + " Clear!", 120 + 3, 320 + 3);

            g2.setColor(Color.WHITE);
            g2.drawString("Stage " + (GameManager.getInstance().level - 1) + " Clear!", 120, 320);
        }

        //  Score
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        if (GameManager.getInstance().score > 0) {
            if (DataManager.getMaxScore() < GameManager.getInstance().score || DataManager.scoreList.isEmpty()) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("Best Score", 33 + 1, 727 + 1);
                g2.setColor(new Color(218, 165, 32));
                g2.drawString("Best Score", 33, 727);
            }
        }

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Score | " + GameManager.getInstance().score, 33 + 1, 750 + 1);
        g2.setColor(Color.BLACK);
        g2.drawString("Score | " + GameManager.getInstance().score, 33, 750);

        //  For Debugging
//        g2.setColor(Color.BLACK);
//        g2.setFont(new Font("Arial", Font.BOLD, 20));
//        g2.drawString("Ball Count : " + GameManager.getInstance().ballCount, 500, 700);
//        g2.drawString("Block Count : " + GameManager.getInstance().blockNum, 500, 720);
    }

    @Override
    public void run() {
        while (true) {
            try {
                //  공의 움직임 및 블럭 삭제 검사
                var it = objects.iterator();
                while (it.hasNext()) {
                    var tmp = it.next();
                    tmp.update(0.066f);

                    //  공의 사라짐 검사
                    if (tmp instanceof Ball) {
                        if (((Ball) tmp).isDead()) {
                            it.remove();
                            GameManager.getInstance().ballCount--;
                        }
                    }
                    else if (tmp instanceof Block) {
                        if (((Block) tmp).getBreak()) {
                            it.remove();
                            GameManager.getInstance().blockNum--;
                        }
                    }
                }

                // 충돌 검사
                var it1 = objects.iterator();
                while (it1.hasNext()) {
                    GameObject tmp1 = it1.next();

                    var it2 = objects.iterator();
                    while (it2.hasNext()) {
                        GameObject tmp2 = it2.next();

                        tmp1.collisionResolution(tmp2);
                    }
                }

                //  Item 추가
                objects.addAll(toAddObjects);
                toAddObjects.clear();

                //  게임 오버 검사
                if (GameManager.getInstance().gameOver()) {
                    GameManager.getInstance().sceneNumber = 2;
                    GameManager.getInstance().getFrame().setScene();
                }

                //  클리어 검사
                if (GameManager.getInstance().stageClear()) {
                    System.out.println("Stage Clear");

                    setFocusable(false);

                    isClear = true;

                    reset();
                    repaint();
                    revalidate();

                    for (int i = 0; i < 5; i++) {
                        clearTextVisible = !clearTextVisible;
                        Thread.sleep(500);
                        repaint();
                    }

                    isClear = false;
                    initGame();

                    setFocusable(true);
                    requestFocus();
                }

                repaint();
                revalidate();

                Thread.sleep(33);
            }
            catch (Exception e) {
                return;
            }
        }
    }
}

public class Main extends JFrame {

    StartScene startScene = null;
    GameScene gameScene = null;
    GameOverScene gameOverScene = null;

    Main() {
        //  윈도우 용
        setSize(815, 800);
        //  Mac 용
        //  setSize(800, 800);
        setTitle("Java Assignment 5");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        GameManager.getInstance().setFrame(this);

        setScene();

        setVisible(true);
    }

    public void setScene() {

        GameManager.getInstance().setInGame(false);

        switch (GameManager.getInstance().sceneNumber) {
            case 0:
                startScene = new StartScene();
                add(startScene);

                if (gameOverScene != null) {
                    remove(gameOverScene);
                }
                break;
            case 1:
                gameScene = new GameScene();
                add(gameScene);

                gameScene.initGame();

                if (startScene != null) {
                    remove(startScene);
                }
                break;
            case 2:
                gameOverScene = new GameOverScene();
                add(gameOverScene);

                if (gameScene != null) {
                    remove(gameScene);
                }

                break;
        }

        revalidate();
    }


    public static void main(String args[]) {
        new Main();
    }

}