import java.lang.Math;

public class Hw1_4 {
    public static void main(String[] args) {
        final int SIZE = 4;

        int[][] arr = new int[4][4];

        //  먼저 임의의 숫자를 채운 후에
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                arr[i][j] = (int) (Math.random() * 10 + 1);
            }
        }

        //  0이 들어갈 위치를 찾는다. (6개로 지정)
        final int CNT = 6;

        for (int i = 0; i < CNT; i++) {
            //  0 ~ SIZE-1 사이의 인덱스를 배출
            int x = (int) (Math.random() * SIZE);
            int y = (int) (Math.random() * SIZE);

            //  0을 가진 Element가 6개가 충족되도록 검사
            if (arr[x][y] != 0) {
                arr[x][y] = 0;
            } else i--; //  만일 0인 원소라면, 다시 진행한다.
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }
}