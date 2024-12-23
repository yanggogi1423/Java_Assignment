import java.util.Scanner;
import java.lang.Math;

public class Hw1_3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("정수 몇 개? ");
        int n = sc.nextInt();

        int[] arr = new int[n];

        for (int i = 0; i < n; i++) {
            //  중복된 Element가 있는지 확인 -> false : 없음 / true : 있음
            boolean flag = false;

            //  1보다 크고 100보다 작은 수를 무작위로 대입
            arr[i] = (int) (Math.random() * 100 + 1);

            for (int j = 0; j < i; j++) {
                if (arr[i] == arr[j]) { //  만일 하나라도 같은 것이 있다면 다시 진행
                    flag = true;
                    break;
                }
            }

            //  만일 true가 된다면, 중복이 있다는 뜻 -> 인덱스를 줄여 다시 진행
            if (flag) {
                i--;
            }
        }

        for (int i = 0; i < n; i++) {
            System.out.print(arr[i] + " ");
            if ((i + 1) % 10 == 0) System.out.println();
        }
    }
}