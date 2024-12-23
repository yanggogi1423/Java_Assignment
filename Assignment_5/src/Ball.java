import java.awt.*;

class Ball extends GameObject {
    float r;
    float vx, vy;

    float spreadAngle = 20f;
    int bullets = 3;

    Ball() {
        x = 400;
        y = 610;
        r = 7;

        if (Math.random() < 0.5) {
            vx = -15;
        } else {
            vx = 15;
        }

        vy = 230;

        color = Color.WHITE;
    }

    boolean isDead() {
        if (y > 800 || x < 0 || x > 830) {
            return true;
        }
        return false;
    }

    @Override
    void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //  그림자
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval((int) (x - r) + 1, (int) (y - r) + 1, (int) (2 * r), (int) (2 * r));

        g2.setColor(color);
        g2.fillOval((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));
    }

    @Override
    void update(float dt) {
        prev_x = x;
        prev_y = y;
        x += vx * dt;
        y += vy * dt;
    }

    @Override
    void collisionResolution(GameObject other) {
        if (other instanceof Wall) {
            Wall wall = (Wall) other;
            float left = wall.x - r;
            float right = wall.x + wall.w + r;
            float top = wall.y - r;
            float bottom = wall.y + wall.h + r;

            if (x > left && x < right && y > top && y < bottom) {

                if (prev_y < top) {
                    y = top - r;
                    vy = -vy;
                }
                if (prev_y > bottom) {
                    y = bottom + r;
                    vy = -vy;
                }
                if (prev_x < left) {
                    x = left - r;
                    vx = -vx;
                }
                if (prev_x > right) {
                    x = right + r;
                    vx = -vx;
                }
            }
        }
        if (other instanceof Player) {
            Player p = (Player) other;

            boolean isCollision = false;

            // Player는 중심 좌표계가 다름
            float left = p.x - p.w / 2 - r;
            float right = p.x + p.w / 2 + r;
            float top = p.y - p.h / 2 - r;
            float bottom = p.y + p.h / 2 + r;

            // 충돌 판정
            if (x > left && x < right && y > top && y < bottom) {
                float overlapLeft = Math.abs(x - left);
                float overlapRight = Math.abs(x - right);
                float overlapTop = Math.abs(y - top);

                //   기존 속력 저장
                float originalScalar = (float) Math.sqrt(vx * vx + vy * vy);

                if (overlapTop < overlapLeft && overlapTop < overlapRight) {
                    //  위쪽 충돌
                    y = top - r;
                    vy = -Math.abs(vy);
                    isCollision = true;
                } else if (overlapLeft < overlapRight) {
                    //  왼쪽 충돌
                    x = left - r;
                    vx = -Math.abs(vx);
                    vy = -Math.abs(vy);
                    isCollision = true;
                } else {
                    //  오른쪽 충돌
                    x = right + r;
                    vx = Math.abs(vx);
                    vy = -Math.abs(vy);
                    isCollision = true;
                }

                if (isCollision) {
                    float relativeX = (x - p.x) / (p.w / 2); // -1 (왼쪽) ~ 1 (오른쪽)

                    float angleAdjustment = relativeX * (float) Math.toRadians(40);

                    //  속도 벡터 회전 (행렬 곱)
                    float newVx = (float) (vx * Math.cos(angleAdjustment) - vy * Math.sin(angleAdjustment));
                    float newVy = (float) (vx * Math.sin(angleAdjustment) + vy * Math.cos(angleAdjustment));

                    vx = newVx;
                    vy = newVy;

                    //  원래 속력 유지
                    float newScalar = (float) Math.sqrt(vx * vx + vy * vy);

                    float scale = originalScalar / newScalar;
                    vx *= scale;
                    vy *= scale;
                }
            }

            if (isCollision) {
                AudioManager.getInstance().startSFX(AudioManager.musicList.Hit);
            }
        }


        if (other instanceof Block) {
            Block b = (Block) other;

            float left = b.x - r;
            float right = b.x + b.w + r;
            float top = b.y - r;
            float bottom = b.y + b.h + r;

            boolean isCollision = false;

            if (x > left && x < right && y > top && y < bottom) {
                if (prev_y < top) {
                    y = top - r;
                    vy = -vy;
                    isCollision = true;
                }
                if (prev_y > bottom) {
                    y = bottom + r;
                    vy = -vy;
                    isCollision = true;
                }
                if (prev_x < left) {
                    x = left - r;
                    vx = -vx;
                    isCollision = true;
                }
                if (prev_x > right) {
                    x = right + r;
                    vx = -vx;
                    isCollision = true;
                }
            }

            if (isCollision) {

                if (b.blockType == 1) {
                    duplicateBall();
                    GameManager.getInstance().score += 20;
                    b.setBreak(true);
                } else if (b.blockType == 2) {
                    AudioManager.getInstance().startSFX(AudioManager.musicList.Hit);
                    b.hp--;

                    GameManager.getInstance().score += 10;

                    if (b.hp <= 0) {
                        b.hp = 0;
                        b.setBreak(true);
                    }
                } else {
                    AudioManager.getInstance().startSFX(AudioManager.musicList.Hit);
                    GameManager.getInstance().score += 20;
                    b.setBreak(true);
                }
            }
        } else {
            return;
        }
    }

    void duplicateBall() {

        AudioManager.getInstance().startSFX(AudioManager.musicList.Duplicate);

        float baseAngle = (float) Math.toDegrees(Math.atan2(vy, vx));
        float dAngle = spreadAngle / (bullets - 1);

        float startAngle = baseAngle - spreadAngle;

        for (int i = 0; i < bullets - 1; i++) {
            float curAngle = startAngle + dAngle * i;
            float radianAngle = (float) Math.toRadians(curAngle);

            Ball newBall = new Ball();

            //  속력 동일하게 해줘야 함
            newBall.vx = (float) (240 * Math.cos(radianAngle));
            newBall.vy = (float) (240 * Math.sin(radianAngle));
            newBall.x = x;
            newBall.y = y;

            GameManager.getInstance().frame.gameScene.toAddObjects.add(newBall);
        }

        GameManager.getInstance().ballCount += 2;
    }
}
