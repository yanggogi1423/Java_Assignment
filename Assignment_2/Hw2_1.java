import java.util.Scanner;

//  좌석 정보를 저장하는 클래스
class SeatInfo {
    //  좌석을 예약한 사람과 예약 유무 변수를 필드에 저장
    private String name;
    private boolean reserve;

    //  기본적으로 예약이 안 된 경우 String은 "---"으로 저장된다.
    SeatInfo() {
        name = new String("---");
        reserve = false;
    }

    //  예약을 했을 때 변경
    public void setInfo(String name) {
        this.name = name;
        reserve = true;
    }

    //  예약을 취소한 경우
    public void cancelReservation() {
        name = "---";
        reserve = false;
    }

    //  name을 리턴
    public String getName() {
        return name;
    }

    //  예약 여부를 리턴
    public boolean isReserve() {
        return reserve;
    }
}

class ConcertReservation {
    //  전역 상수로 사용하기 위해 선언
    static final int MAX_SEAT = 10;

    private Scanner sc;

    //  예약 좌석 정보
    private SeatInfo[][] arr;
    //  pCnt는 각 좌석 라인(S, A, B)의 예약자 수이다.
    private int[] pCnt;

    //  생성자의 매개변수로 Scanner를 받아 저장하여, 클래스 내부에서 지속 사용하게 한다.
    ConcertReservation(Scanner sc) {
        this.sc = sc;

        arr = new SeatInfo[3][MAX_SEAT];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < MAX_SEAT; j++) {
                arr[i][j] = new SeatInfo();
            }
        }

        //  총 3개 라인이므로 생성 후 0으로 초기화
        pCnt = new int[3];
        for (int i = 0; i < 3; i++) {
            pCnt[i] = 0;
        }

        System.out.println("명품콘서트홀 예약 시스템입니다.");
    }

    //  프로그램을 while로 무한 반복하여 실행
    public void reserveProgram() {
        while (true) {
            if (!getMode()) {
                sc.close();
                break;
            }
        }
    }

    //  프로그램 종료를 위해 boolean을 반환한다.
    private boolean getMode() {
        int mode;

        while (true) {
            System.out.print("예약 : 1, 조회 : 2, 취소 : 3, 끝내기 : 4 >> ");
            mode = sc.nextInt();

            if (mode < 1 || mode > 4) {
                System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
            } else break;
        }

        switch (mode) {
            case 1:     //  예약
                reserve();
                break;
            case 2:     //  조회
                for (int i = 0; i < 3; i++) printSeat(i);
                System.out.println("<<< 조회가 완료되었습니다. >>>");
                break;
            case 3:     //  취소
                cancelReservation();
                break;
            case 4:
                //  프로그램 종료
                return false;
        }
        return true;
    }

    //  예약 프로그램
    private void reserve() {
        //  모든 좌석이 꽉 찬 경우
        if (pCnt[0] + pCnt[1] + pCnt[2] >= 3 * MAX_SEAT) {
            System.out.println("전 좌석이 가득찼습니다. 추가 예매가 불가능합니다.");
            return;
        }

        int mode;
        //  변수 mode를 이용해 Handling, 잘못된 입력에 대응하기 위해 반복문 사용
        while (true) {
            System.out.print("좌석구분 S(1), A(2), B(3) >> ");
            mode = sc.nextInt();

            if (mode < 1 || mode > 4) {  //  mode는 1 ~ 3
                System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
            } else if (pCnt[mode] >= MAX_SEAT) {    //  선택된 라인이 가득 찼을 때
                System.out.println("선택된 라인이 모두 예약되었습니다. 다른 라인을 선택해주세요.");
            } else break;
        }

        //  선택된 라인의 상태를 보여줌
        printSeat(mode - 1);

        System.out.print("이름 >> ");
        String name = new String(sc.next());

        int seat;   //  seat이라는 변수로 Handling
        while (true) {
            System.out.print("번호 >> ");
            seat = sc.nextInt();

            if (seat <= 0 || seat > MAX_SEAT) {  //  seat 번호는 1 ~ 10
                System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
            } else if (arr[mode - 1][seat - 1].isReserve()) {   //  이미 예약이 된 경우
                System.out.println("이미 예약되어있는 좌석입니다. 다시 입력해주세요.");
            } else {    //  예약
                arr[mode - 1][seat - 1].setInfo(name);
                pCnt[mode]++;
                break;
            }
        }
    }

    //  취소
    private void cancelReservation() {
        if (pCnt[0] + pCnt[1] + pCnt[2] == 0) { //  단 한 건의 예약도 존재하지 않을 때
            System.out.println("예약자 명단이 공백입니다. 먼저 예약을 진행해주세요.");
            return;
        }

        int mode;
        while (true) {
            System.out.print("좌석 S(1), A(2), B(3) >> ");
            mode = sc.nextInt();

            if (mode < 1 || mode > 4) {  //  mode는 1 ~ 3
                System.out.println("잘못된 번호입니다. 다시 입력해주세요.");
            } else break;
        }

        //  취소하고자 하는 좌석의 라인을 보여줌
        printSeat(mode - 1);

        String name = new String();

        while (true) {
            System.out.print("이름 >> ");
            name = sc.next();

            if (!findReservation(name)) {   //  명단에 이름이 존재하지 않는 경우
                System.out.println("명단에 없는 이름입니다. 다시 입력해주세요.");
            } else break;
        }
    }

    //  예약 취소자의 이름을 입력 받고 예약 여부에 대한 boolean을 반환
    private boolean findReservation(String name) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < MAX_SEAT; j++) {
                if (name.equals(arr[i][j].getName())) {
                    arr[i][j].cancelReservation();
                    pCnt[i]--;  //  해당 라인의 Count를 1 줄임
                    return true;
                }
            }
        }
        //  존재하지 않는 경우
        return false;
    }

    //  좌석 라인 출력(입력된 인덱스 c에 해당하는 줄을 찾아 print)
    private void printSeat(int c) {
        //  함수 재활용을 위한 배열
        char[] lines = {'S', 'A', 'B'};

        System.out.print(lines[c] + ">> ");
        for (var e : arr[c]) {
            System.out.print(e.getName() + " ");
        }
        System.out.println();
    }
}

public class Hw2_1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ConcertReservation cr = new ConcertReservation(sc);

        //  프로그램 시작
        cr.reserveProgram();
    }
}