import java.awt.*;
import java.awt.event.KeyEvent;

class Player extends GameObject {
    int w, h;

    static final float VX = 300;
    boolean isLeft, isRight;

    //  origin
    float ox, oy;

    Player() {
        this.x = ox = 400;
        this.y = oy = 650;
        this.color = Color.ORANGE;

        this.w = 150;
        this.h = 30;

        isLeft = false;
        isRight = false;
    }

    void move(int key) {
        if (key == KeyEvent.VK_LEFT) {
            if (x - w / 2 < Wall.offset) {
                isLeft = false;
                return;
            }
            isLeft = true;
        }
        if (key == KeyEvent.VK_RIGHT) {
            if (x + w / 2 > 800 - Wall.offset) {
                isRight = false;
                return;
            }
            isRight = true;
        }
    }

    void setOrigin() {
        this.x = ox;
        this.y = oy;
        isLeft = false;
        isRight = false;
    }

    @Override
    void update(float dt) {
        prev_x = x;

        if (isLeft) {
            x -= VX * dt;
        }
        else if (isRight) {
            x += VX * dt;
        }
        else return;

        if (x - w / 2 < Wall.offset || x + w / 2 > 800 - Wall.offset) {
            x = prev_x;
        }
    }

    @Override
    void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int R = color.getRed() -50;     if(R < 0) R = 0;
        int G = color.getGreen() -50;   if(G < 0) G = 0;
        int B = color.getBlue() -50;    if(B < 0) B = 0;

        g2.setColor(new Color(R, G, B, 255));
        g2.fillRoundRect((int) (x - w / 2), (int) (y - h / 2), w, h, 3, 3);

        g2.setColor(color);
        g2.fillRoundRect((int) (x - w / 2) , (int) (y - h / 2) , w - 3, h - 3, 3, 3);
    }
}
