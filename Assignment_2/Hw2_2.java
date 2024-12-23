
//  교재에 나와있는 추상 클래스 pair
abstract class PairMap {
    protected String keyArray[];
    protected String valueArray[];

    abstract String get(String key);

    abstract void put(String key, String value);

    abstract String delete(String key);

    abstract int length();
}

//  위의 추상 클래스 PairMap을 상속받는 Dictionary Class
class Dictionary extends PairMap {
    private int items;

    //  길이를 매개변수로 삼아 생성
    Dictionary(int leng) {
        keyArray = new String[leng];
        valueArray = new String[leng];

        for (int i = 0; i < leng; i++) {
            keyArray[i] = new String();
            valueArray[i] = new String();
        }

        //  저장된 아이템의 개수
        items = 0;
    }

    @Override
    String get(String key) {    //  입력된 키에 해당하는 값을 반환
        String tmp = null;      //  null로 초기화하여 없으면 null이 반환되도록 한다.
        for (int i = 0; i < keyArray.length; i++) {
            if (keyArray[i].equals(key)) {
                tmp = valueArray[i];
                break;
            }
        }
        return tmp;
    }

    @Override
    void put(String key, String value) {    //  입력받은 key와 value를 pair map에 저장
        for (int i = 0; i < keyArray.length; i++) {
            if (keyArray[i].equals(key)) {  //  만일 입력된 key가 이미 존재한다면 값만 수정하고 return
                valueArray[i] = value;
                return;
            }
        }

        for (int i = 0; i < keyArray.length; i++) { //  만일 입력된 키가 존재하지 않는다면 ""와 비교(비어있는 곳 찾기)
            if (keyArray[i].equals("")) {
                keyArray[i] = key;
                valueArray[i] = value;
                items++;
                break;
            }
        }
    }

    @Override
    String delete(String key) { //  입력받은 키에 해당하는 아이템을 삭제
        String tmp = null;      //  없을 수도 있으니 null로 초기화
        for (int i = 0; i < keyArray.length; i++) {
            if (keyArray[i].equals(key)) {  //  만일 같은 것이 있다면
                keyArray[i] = "";

                tmp = valueArray[i];
                valueArray[i] = "";

                items--;    //  아이템의 숫자를 1감소
                break;
            }
        }
        return tmp;
    }

    @Override
    int length() {  //  아이템의 개수를 반환
        return items;
    }
}

public class Hw2_2 {
    public static void main(String[] args) {
        Dictionary dic = new Dictionary(10);

        dic.put("황기태", "자바");
        dic.put("이재문", "파이썬");
        dic.put("이재문", "C++");

        System.out.println("이재문의 값은 " + dic.get("이재문"));
        System.out.println("황기태의 값은 " + dic.get("황기태"));
        dic.delete("황기태");
        System.out.println("황기태의 값은 " + dic.get("황기태"));
    }
}