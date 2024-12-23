import java.util.Scanner;

//  SLL(Single Linked List)를 클래스로 구현
//  도형에 대한 기본 틀인 추상 클래스
abstract class Shape {
    private Shape next;

    //  다음 노드의 주소를 null로 초기화하며 생성
    public Shape() {
        next = null;
    }
    //  다음 노드의 주소를 설정
    public void setNext(Shape next) {
        this.next = next;
    }
    //  도형의 다음 주소를 반환
    public Shape getNext() {
        return next;
    }
    //  서브 클래스에서 구현해야하는 추상메소드
    public abstract void draw();
}

//  Line인 경우
class Line extends Shape {
    public void draw() {
        System.out.println("Line");
    }
}

//  Rect인 경우
class Rect extends Shape {
    public void draw() {
        System.out.println("Rect");
    }
}

//  Circle인 경우
class Circle extends Shape {
    public void draw() {
        System.out.println("Circle");
    }
}

//  전체를 관리하는 Graphic 클래스
class GraphicEditor {
    private Shape head; //  가장 처음의 노드의 주소(head는 Dummy Node로 사용되지 않았다.)
    Scanner sc;         //  Scanner 사용을 클래스에서 지속적으로 하기 위한 레퍼런스(생성 시 초기화)

    GraphicEditor(Scanner sc) {
        head = null;
        this.sc = sc;

        System.out.println("그래픽 에디터 beauty을 실행합니다.");
    }

    //  프로그램 실행
    public void startProgram() {
        while(true){    //  반복할 수 있도록 설계
            if(!getMode()){
                sc.close();
                break;
            }
        }

        System.out.println("beauty를 종료합니다.");
        System.exit(1);
    }

    //  어떤 작업을 수행할 지 선택하는 함수
    private boolean getMode() {
        System.out.print("삽입(1), 삭제(2), 모두 보기(3), 종료(4) >> ");
        int mode = sc.nextInt();

        switch (mode) {
            case 1:
                //  삽입
                insertShape();
                break;
            case 2:
                //  삭제
                deleteShape();
                break;
            case 3:
                //  모두 보기
                drawAll();
                break;
            case 4:
                //  프로그램 종료
                return false;
        }

        return true;
    }

    //  새로운 도형을 삽입
    private void insertShape() {
        System.out.print("Line(1), Rect(2), Circle(3) >> ");
        int mode = sc.nextInt();

        Shape buff = null;

        //  입력받은 mode 값에 알맞은 도형 객체를 생성
        if (mode == 1) {
            Line line = new Line();
            buff = line;
        } else if (mode == 2) {
            Rect rect = new Rect();
            buff = rect;
        } else if (mode == 3) {
            Circle circle = new Circle();
            buff = circle;
        } else {    //  잘못된 번호가 들어온 경우 return하도록
            System.out.println("Invalid Number");
            return;
        }

        //  다음 노드를 찾는다.
        if (head == null) { //  head가 비어있다면,
            head = buff;
        } else {
            Shape finder = head;
            while (true) {
                if (finder.getNext() == null) { //  다음 노드의 위치가 비어있는 노드까지 도달
                    finder.setNext(buff);
                    break;
                }
                finder = finder.getNext();
            }
        }
    }

    //  도형 삭제
    private void deleteShape() {
        System.out.print("삭제할 도형의 위치 >> ");
        int idx = sc.nextInt();

        //  실제 삭제할 위치는 idx-1이기에 줄여준다. 인덱스는 0부터이다.
        idx--;

        Shape finder = head;
        Shape buff = finder;

        for (int i = 0; i <= idx; i++) {
            if (finder == null) {   //  도달할 수 없는 위치라면(그 위치에 도형이 존재하지 않음)
                System.out.println("삭제할 수 없습니다.");
                return;
            }
            buff = finder;  //  위치를 받아오기 위해 추가적으로 buff가 finder의 전 상태를 저장한다.
            finder = finder.getNext();
        }

        //  만일 head가 대상이라면, buff의 다음 노드의 주소를 받는다.
        if(buff == head){
            head = buff.getNext();
        }
        else{   //  finder가 null이 아닌 경우
            if(finder!=null){
                buff.setNext(finder.getNext());
                finder.setNext(null);
            }   //  finder가 null인 경우(즉 리스트의 끝)
            else buff.setNext(finder);
        }
    }

    //  도형을 모두 그리기
    private void drawAll() {
        Shape finder = head;
        if (finder == null) {   //  리스트가 비어있는 경우
            System.out.println("도형 리스트가 비어있습니다. 먼저 도형을 추가해주세요.");
            return;
        }

        while (true) {
            finder.draw();
            finder = finder.getNext();
            if (finder == null) return;
        }
    }

}

public class Hw2_3 {
    public static void main(String args[]) {
        GraphicEditor ge = new GraphicEditor(new Scanner(System.in));

        //  프로그램 시작
        ge.startProgram();
    }
}