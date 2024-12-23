import java.util.Scanner;

//  원에 대한 class
class MyCircle {
    public int x, y;
    public int radius;

    MyCircle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}

public class Hw1_1 {
    //  거리를 계산하여 double 값을 리턴하는 함수
    static double calDist(MyCircle c1, MyCircle c2) {
        return Math.sqrt((c1.x - c2.x) * (c1.x - c2.x) + (c1.y - c2.y) * (c1.y - c2.y));
    }

    //  두 개의 원 객체를 입력 받고 정해진 문자열을 출력하는 함수
    static void judgeDist(MyCircle c1, MyCircle c2) {
        double dist = calDist(c1, c2);

        if (dist > c1.radius + c2.radius) {
            System.out.println("두 원이 서로 겹치지 않는다.");
        } else {
            System.out.println("두 원이 서로 겹친다.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        MyCircle[] c = new MyCircle[2];

        for (int i = 0; i < 2; i++) {
            System.out.print((i + 1) + "번째 원의 중심과 반지름 입력 >> ");
            c[i] = new MyCircle(sc.nextInt(), sc.nextInt(), sc.nextInt());
        }

        sc.close();

        judgeDist(c[0], c[1]);
    }
}