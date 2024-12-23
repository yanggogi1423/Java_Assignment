import java.util.Scanner;

public class Hw1_2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("연산 >> ");
        double n1 = sc.nextDouble();
        String op = sc.next();
        double n2 = sc.nextDouble();

        sc.close();

        double res = 0;

        //  String같은 경우 Switch문에 그대로 넣어도 eqauls의 기능이 자동으로 수행된다.
        switch (op) {
            case "+":
                res = n1 + n2;
                break;
            case "-":
                res = n1 - n2;
                break;
            case "*":
                res = n1 * n2;
                break;
            case "/":
                //  0으로 나눈 경우
                if (n2 == 0.0) {
                    System.out.println("0으로 나눌 수 없습니다.");
                    System.exit(-1);    //  프로그램 종료
                }
                res = n1 / n2;
                break;
            //  잘못된 연산 기호가 들어왔을 때
            default:
                System.out.println("옳지 않은 연산 기호입니다.");
                System.exit(-1);
        }

        //  출력을 문제의 test case와 같이 하기 위해서 다음 출력 방식을 가짐
        if (n1 == (int) n1) System.out.print(Integer.toString((int) n1) + op);
        else System.out.print(Double.toString(n1) + op);

        if (n2 == (int) n2) System.out.print(Integer.toString((int) n2) + "의 계산 결과는 ");
        else System.out.print(Double.toString(n2) + "의 계산 결과는 ");

        if (res == (int) res) System.out.print((int) res);
        else System.out.print(res);
    }
}