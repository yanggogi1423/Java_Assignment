import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

abstract class GameObject {
    float x, y;
    float prev_x, prev_y;
    Color color;

    GameObject() {

    }

    abstract void draw(Graphics g);

    void update(float dt) {

    }

    void collisionResolution(GameObject other) {

    }
}

//  Static Object
class Wall extends GameObject {
    float w, h;

    static final int offset = 20;

    boolean isHorizontal;

    Wall(int x, int y, int w, int h, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.isHorizontal = isHorizontal;

    }

    @Override
    void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (isHorizontal) {
            GradientPaint gp = new GradientPaint(x, y, Color.LIGHT_GRAY, x + w / 2, y, Color.DARK_GRAY);
            g2.setPaint(gp);
            g2.fillRect((int) x, (int) y, (int) (w / 2), (int) h);

            gp = new GradientPaint(x + w / 2, y, Color.DARK_GRAY, x + w, y, Color.LIGHT_GRAY);
            g2.setPaint(gp);
            g2.fillRect((int) (x + w / 2), (int) y, (int) (w / 2), (int) h);
        }
        else {
            GradientPaint gp = new GradientPaint(x, y, Color.LIGHT_GRAY, x, y + h, Color.DARK_GRAY);
            g2.setPaint(gp);
            g2.fillRect((int) (x), (int) y, (int) (w), (int) h);
        }
    }
}

class Block extends GameObject implements Runnable {

    boolean isBreak;
    //  0 : Default, 1 : Special, 2 : Hard
    int blockType;

    float w, h;
    int hp;

    //  Special Block
    float alpha;

    BufferedImage img;

    Block() {
        //  Random
        if (Math.random() < 0.65) {
            blockType = 0;
            try {
                img = ImageIO.read(getClass().getClassLoader().getResource("DefaultBlock.png"));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (Math.random() >= 0.65 && Math.random() < 0.9) {
            blockType = 2;
            hp = 3;

            try {
                img = ImageIO.read(getClass().getClassLoader().getResource("HardBlock.png"));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            blockType = 1;
            alpha = 0;
            try {
                img = ImageIO.read(getClass().getClassLoader().getResource("SpecialBlock.png"));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            Thread th = new Thread(this);
            th.start();
        }

        isBreak = false;
    }

    @Override
    void draw(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect((int) x + 2, (int) y + 2, (int) (w), (int) (h));

        if (blockType == 0) {
            g2.drawImage(img, (int) x, (int) y, (int) w, (int) h, null);
        }
        else if (blockType == 1) {
            g2.drawImage(img, (int) x, (int) y, (int) w, (int) h, null);

            Composite originalComposite = g2.getComposite();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.WHITE);
            g2.fillRect((int) x, (int) y, (int) w, (int) h);

            g2.setComposite(originalComposite);
        }
        else {
            Composite originalComposite = g2.getComposite();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hp / 3f));
            g2.drawImage(img, (int) x, (int) y, (int) w, (int) h, null);

            g2.setComposite(originalComposite);
        }
    }

    void setBreak(boolean isBreak) {
        this.isBreak = isBreak;
    }

    boolean getBreak() {
        return isBreak;
    }

    void setBlock(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public void run() {
        boolean handler = true;

        while (true) {

            try {

                Thread.sleep(10);

                if (handler) {
                    alpha -= 0.02f;
                }
                else {
                    alpha += 0.02f;
                }

                if (alpha <= 0) {
                    alpha = 0;
                    handler = false;
                }
                else if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    handler = true;
                }
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

class FlickeringStar extends GameObject implements Runnable {

    Color color;
    boolean isBright;
    int interval;

    int r;

    FlickeringStar() {
        this.x = (int) (Math.random() * 801);
        this.y = (int) (Math.random() * 801);

        color = new Color(
                (int) (Math.random() * 256),
                (int) (Math.random() * 256),
                (int) (Math.random() * 256));

        r = (int) (Math.random() * 3.5 + 1);

        interval = (int) (Math.random() * 50 + 50);
        isBright = true;

        Thread th = new Thread(this);

        th.start();
    }

    @Override
    void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (isBright) {
            g2.setColor(color);
        }
        else {
            g2.setColor(Color.darkGray);
        }

        g2.fillOval((int) x, (int) y, r, r);
    }

    @Override
    public void run() {
        while (true) {
            try {
                isBright = !isBright;
                Thread.sleep(interval);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class FallSnow extends GameObject {

    float vy;
    int r;

    float deltaT;

    FallSnow() {
        this.x = (int) (Math.random() * 801);
        this.y = 0;

        vy = (int) (Math.random() * 50 + 50);
        deltaT = (float) Math.random() / 1000f + 0.011f;

        r = (int) (Math.random() * 3.5 + 1);
    }

    @Override
    void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.white);

        g2.fillOval((int) x, (int) y, r, r);
    }

    boolean isFallDown(){
        if(y > 800){
            return true;
        }
        return false;
    }

    @Override
    void update(float dt) {
        y += vy * deltaT;
    }
}