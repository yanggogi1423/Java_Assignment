import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

class Hw3Panel extends JPanel {
    //  색 저장
    Color color;

    //  0 : 아무것도 없음, 1 : 회색으로 위로, 2 : 검정으로 위로, 3: 하얗게 위로 (그라데이션 효과)
    int mode;

    Hw3Panel(Color color, int mode) {
        this.color = color;
        this.mode = mode;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);

        GradientPaint gp;

        //  mode 값에 따라 각기 다른 그라데이션 효과를 부여
        if (mode == 1) {
            gp = new GradientPaint(0, getHeight(), color, 0, 0, Color.LIGHT_GRAY);
            g2.setPaint(gp);
        }
        else if (mode == 2) {
            gp = new GradientPaint(0, getHeight(), color, 0, 0, Color.GRAY);
            g2.setPaint(gp);
        }
        else if(mode == 3){
            gp = new GradientPaint(0, getHeight(), color, 0, 0, Color.WHITE);
            g2.setPaint(gp);
        }

        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}

class Hw3Button extends JButton {
    //  그림자 효과를 주기 위한 Offset 값
    static final int shadowOffsetX = -1;
    static final int shadowOffsetY = 1;

    Hw3Button(String text) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, JavaHW3.BUTTON_FONT));

        //  글자 색상 변경
        //  'C' 버튼만 색상이 다름
        if (text == "C") {
            setForeground(new Color(189, 124, 104));
        }
        else {
            setForeground(new Color(128, 120, 102));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        //  안티 엘리에이싱 효과
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //  배경 생상
        g2.setColor(new Color(228, 220, 202));
        //  버튼 내에 RoundRect 생성
        g2.fillRoundRect(7, 7, getWidth() - 15, getHeight() - 15, 15, 15);

        //  Font의 크기를 정밀하게 알기 위한 FontMetrics (출처 : 구글 및 GPT) -> 그림자 배치용
        FontMetrics metrics = g2.getFontMetrics(getFont());

        //  글씨의 그림자는 White 색상
        g2.setColor(Color.WHITE);
        g2.drawString(getText(), (getWidth() - metrics.stringWidth(getText())) / 2 + shadowOffsetX, (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2 + shadowOffsetY);

        //  기존 색상을 가져오고 본 글자 칠하기
        g2.setColor(getForeground());
        g2.drawString(getText(), (getWidth() - metrics.stringWidth(getText())) / 2, (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2);
    }
}

class Hw3Label extends JLabel {
    static final int shadowOffsetX = -1;
    static final int shadowOffsetY = 1;

    Hw3Label(String text) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, JavaHW3.DISPLAY_FONT));

        //  글자 색 변경
        setForeground(new Color(52, 60, 51));
    }

    @Override
    protected void paintComponent(Graphics g) {
//        super.paintComponent(g); 은 생략한다.
        //  Label의 글씨와 위치 보정을 수동으로 하고 있고, 위의 method를 호출할 시, 일정 글자 수가 넘어가면 문제가 발생

        Graphics2D g2 = (Graphics2D) g;
        //  안티 엘리에이싱 효과
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //  Font의 크기를 정밀하게 알기 위한 FontMetrics -> 그림자 배치용
        FontMetrics metrics = g2.getFontMetrics(getFont());

        //  현재 텍스트를 가져오기
        String text = getText();
        //  전체 string의 폭을 가져오기
        int textWidth = metrics.stringWidth(text);

        //  getInsets() : 상하좌우 여백을 가져올 수 있음
        //  getAscent() : 기본 폭에서 상승된 만큼의 폭을 가져옴
        //  getDescent() : 기본 폭에서 하강된 만큼의 폭을 가져옴
        int x = getWidth() - textWidth - getInsets().right; // 오른쪽 정렬
        int y = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;

        // 그림자 텍스트 그리기
        g2.setColor(Color.WHITE);
        g2.drawString(text, x + shadowOffsetX, y + shadowOffsetY);

        // 본문 텍스트 그리기
        g2.setColor(getForeground());
        g2.drawString(text, x, y);
    }
}

public class JavaHW3 extends JFrame {
    static final double ASPECT_RATIO = 3.0 / 4.0;   // 화면 비율: 3:4
    static final int INITIAL_WIDTH = 450;   // 가로 초기 크기 (디버깅 용으로 존재)
    static final int INITIAL_HEIGHT = 600;  // 세로 초기 크기 (디버깅 용)
    static final int DISPLAY_FONT = 100;    //  디버깅용
    static final int BUTTON_FONT = 80;      //  디버깅용

    Stack<Integer> calculator;  //  계산 결과를 담는 Stack 자료구조
    static String buff;         //  현재 가지고 있는 입력 창
    static String curOperator;  //  마지막으로 입력된 혹은 현재의 연산자 상태

    //  전체를 담는 패널
    Hw3Panel calculatorPanel;

    Hw3Panel upperWrapper; //   디스플레이 영역
    Hw3Panel lowerWrapper; //   버튼 영역

    Hw3Panel displayBackgroundPanel;    //  디스플레이 배경 패널
    Hw3Panel displayPanel;              //  디스플레이 패널
    Hw3Label displayLabel;              //  디스플레이 라벨

    //  버튼의 text를 담고 있는 String 배열
    String[] buttons = {
            "7", "8", "9", "C",
            "4", "5", "6", "+",
            "1", "2", "3", "-",
            "0", "", "", "="
    };

    //  버튼을 모두 담고 있는 List
    Hw3Button[] buttonList;

    //  초기 설정
    private void init() {
        setTitle("Java HW3");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); //   JFrame의 레이아웃을 BorderLayout으로 설정
        setBackground(Color.BLACK);
        setResizable(true);

        //  초기값 설정
        buff = "0";
        curOperator = "";
        calculator = new Stack<>();
        calculator.clear();

        calculatorPanel = new Hw3Panel(Color.WHITE, 0);
        calculatorPanel.setLayout(new GridBagLayout()); //  GridBagLayout을 사용하여 내부 컴포넌트 배치
    }

    //  디스플레이 영역인 upperWrapper 설정
    private void setUpperWrapper() {
        upperWrapper = new Hw3Panel(Color.GRAY, 3);
        upperWrapper.setLayout(new GridBagLayout());    //  Wrapper의 Layout 설정

        //  디스플레이 Background Panel
        displayBackgroundPanel = new Hw3Panel(new Color(101, 101, 101), 1);
        displayBackgroundPanel.setLayout(new BorderLayout());

        //  디스플레이 Panel
        displayPanel = new Hw3Panel(new Color(151, 159, 150), 2);
        displayPanel.setLayout(new BorderLayout());

        //  디스플레이 Label
        displayLabel = new Hw3Label("0");
        displayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        displayLabel.setVerticalAlignment(SwingConstants.CENTER);

        //  각 Panel에 Padding 추가
        upperWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        displayPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // 디스플레이 내부 패딩
        displayBackgroundPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // 배경 패딩

        displayPanel.add(displayLabel, BorderLayout.CENTER);
        displayBackgroundPanel.add(displayPanel, BorderLayout.CENTER);

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        //  시작 위치
        gbc.gridx = 0;
        gbc.gridy = 0;
        //  확장 비율
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        //  크기 변경 적용
        gbc.fill = GridBagConstraints.BOTH;

        upperWrapper.add(displayBackgroundPanel, gbc);
    }

    //  버튼 영역인 lowerWrapper 영역 설정
    private void setLowerWrapper() {
        lowerWrapper = new Hw3Panel(Color.LIGHT_GRAY, 0);
        lowerWrapper.setLayout(new GridLayout(4, 4, 0, 0)); //   4x4 그리드

        buttonList = new Hw3Button[buttons.length];

        for (int i = 0; i < buttons.length; i++) {
            buttonList[i] = new Hw3Button(buttons[i]);

            //  버튼을 감싸고 있는 Wrapper 생성
            JPanel buttonWrapper = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            buttonWrapper.setLayout(new BorderLayout());
            buttonWrapper.setBorder(new EmptyBorder(1, 1, 1, 1));   //  버튼 Wrapper의 padding 설정
            buttonWrapper.add(buttonList[i], BorderLayout.CENTER);

            //  버튼에 기능 추가
            if (buttons[i].equals("")) {
                //  하단에 아무것도 없는 버튼을 비활성화 시킴
                buttonList[i].setVisible(false);
            }
            else {   // 숫자 및 연산자 버튼에 대한 액션 리스너 설정
                switch (buttons[i]) {
                    //  숫자 키
                    case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9":

                        //  익명 클래스에서의 effectively final을 위한 변수
                        int finalNum = i;

                        //  lambda expression 사용
                        buttonList[i].addActionListener((e) -> {

                            //  buff가 "0"일 때 "0"을 없애기 위한 코드
                            if (buff.equals("0")) {
                                buff = buttons[finalNum];
                            }
                            else {
                                buff += buttons[finalNum];
                            }

                            displayLabel.setText(buff);
                        });
                        break;
                        //  Clear 키
                    case "C":
                        buttonList[i].addActionListener((e) -> {
                            //  Stack, buff, operator 모두 비우기
                            calculator.clear();
                            buff = "0";
                            curOperator = "";

                            System.out.println("Clear All");

                            displayLabel.setText(buff);
                        });
                        break;
                        //  + 연산자
                    case "+":
                        buttonList[i].addActionListener((e) -> {

                            //  마지막 혹은 현재 연산자 변경
                            curOperator = "+";

                            //  Stack이 비어있을 때 -> 첫 입력
                            if (calculator.isEmpty()) {
                                //  현재 buff에 있는 String을 int로 변환 후 Stack에 저장
                                calculator.push(Integer.parseInt(buff));

                                //  buff 초기화
                                buff = "0";
                            }
                            else {
                                //  만일 buff가 "0"이라면 입력 무시
                                if (Integer.parseInt(buff) == 0) {
                                    return;
                                }

                                //  계산 용이성을 위해 Stack에 push
                                calculator.push(Integer.parseInt(buff));

                                int temp1 = calculator.pop();
                                int temp2 = calculator.pop();

                                calculator.push(temp1 + temp2);
                                buff = temp1 + temp2 + "";

                                displayLabel.setText(buff);
                                //  연산 결과 stack에 저장후 buff 초기화
                                buff = "0";
                            }

                        });
                        break;
                        //  - 연산자
                    case "-":
                        buttonList[i].addActionListener((e) -> {
                            //  + 연산자와 원리는 동일하기에 주석은 생략

                            curOperator = "-";

                            if (calculator.isEmpty()) {
                                calculator.push(Integer.parseInt(buff));
                                buff = "0";

                            }
                            else {
                                if (Integer.parseInt(buff) == 0) {
                                    return;
                                }
                                calculator.push(Integer.parseInt(buff));

                                //  temp2가 빼지는 값이다. (마지막으로 Stack에 들어온 값)
                                int temp2 = calculator.pop();
                                int temp1 = calculator.pop();

                                calculator.push(temp1 - temp2);
                                buff = temp1 - temp2 + "";

                                displayLabel.setText(buff);
                                buff = "0";

                            }
                        });
                        break;
                        //  = 연산자
                    case "=":
                        buttonList[i].addActionListener((e) -> {
                            //  만일 stack이 비어있거나, buff가 "0", 즉 비어있는 상태라면 연산자를 무시하도록 (오류 방지)
                            if (calculator.isEmpty() || buff.equals("0")) return;

                            //  만일 현재 연산자가 "", 즉 비어있거나 마지막 입력이 "=" 연산자였을 경우 연산자를 무시 (오류 방지)
                            if (curOperator.equals("")) return;

                            //  굳이 stack에 넣지 않고 처리 (+, - 연산자와의 차이)
                            int temp1 = calculator.pop();
                            int temp2 = Integer.parseInt(buff);

                            if (curOperator.equals("+")) {
                                calculator.push(temp1 + temp2);
                                buff = temp1 + temp2 + "";

                                displayLabel.setText(buff);
                                buff = "0";
                                curOperator = "";
                            }
                            else if (curOperator.equals("-")) {
                                calculator.push(temp1 - temp2);
                                buff = temp1 - temp2 + "";

                                displayLabel.setText(buff);
                                buff = "0";
                                curOperator = "";
                            }

                        });
                        break;
                }
            }

            //  버튼을 갖고 있는 버튼 Wrapper를 lowerWrapper에 추가
            lowerWrapper.add(buttonWrapper);
        }
    }

    //  폰트 크기 및 비율 자동 변경
    private void adjustComponents(int panelWidth, int panelHeight) {
        //  폰트 크기 비율 계산
        int newDisplayFontSize = panelHeight / 7;
        displayLabel.setFont(new Font("Arial", Font.PLAIN, newDisplayFontSize));

        int newButtonFontSize = panelHeight / 12;
        for (var button : buttonList) {
            button.setFont(new Font("Arial", Font.BOLD, newButtonFontSize));
        }

        //  패딩 조정 (디스플레이 패널) -> 패딩을 조절하면 알아서 창이 바뀐다.
        int newDisplayPanelPadding = panelWidth / 40;
        //  최소 크기 제한 (화면을 너무 줄일 시 없어질 수 있음)
        if (newDisplayPanelPadding < 5) newDisplayPanelPadding = 5;
        displayPanel.setBorder(new EmptyBorder(newDisplayPanelPadding, newDisplayPanelPadding, newDisplayPanelPadding, newDisplayPanelPadding));

        //  패딩 조정 (디스플레이 Background Panel)
        int newDisplayBackgroundPadding = panelWidth / 30;
        //  최소 크기 제한
        if (newDisplayBackgroundPadding < 5) newDisplayBackgroundPadding = 5;
        displayBackgroundPanel.setBorder(new EmptyBorder(newDisplayBackgroundPadding, newDisplayBackgroundPadding, newDisplayBackgroundPadding, newDisplayBackgroundPadding));

        //  upperWrapper 패딩 조절 (내부 패널의 패딩을 기준으로 조정)
        upperWrapper.setBorder(new EmptyBorder(newDisplayBackgroundPadding / 3, newDisplayBackgroundPadding / 3, newDisplayBackgroundPadding / 3, newDisplayBackgroundPadding / 3));

        //  lowerWrapper 패딩 조절 (내부 패널의 패널을 기준으로 조정)
        lowerWrapper.setBorder(new EmptyBorder(newDisplayBackgroundPadding / 15, newDisplayBackgroundPadding / 15, newDisplayBackgroundPadding / 15, newDisplayBackgroundPadding / 15));

        //  재배치
        calculatorPanel.revalidate();
        calculatorPanel.repaint();
    }

    //  창 크기가 변해도 3 : 4을 유지하도록 함
    private void maintainAspectRatio() {
        //  Content pane의 크기 가져오기 (Content pane을 사용한 이유는 보고서에 기술)
        int frameWidth = getContentPane().getWidth();
        int frameHeight = getContentPane().getHeight();

        //  계산기 패널의 최대 크기 계산 (3:4 비율 유지) -> width, height을 비교하여 가장 큰 값을 기준으로 설정
        int newWidth = frameWidth;
        int newHeight = (int) (newWidth / ASPECT_RATIO);

        if (newHeight > frameHeight) {
            newHeight = frameHeight;
            newWidth = (int) (newHeight * ASPECT_RATIO);
        }

        //  계산기 패널의 크기 설정 (실제 크기 설정을 위한 Preferred size 설정)
        calculatorPanel.setPreferredSize(new Dimension(newWidth, newHeight));

        //  내부 요소들에 대한 재배치
        adjustComponents(newWidth, newHeight);

        //  전체 레이아웃 다시 적용
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    //  Layout 설정
    private void setLayout() {
        //  디스플레이 영역 추가 (비율 : 30%)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        calculatorPanel.add(upperWrapper, gbc);

        //  버튼 영역 추가 (비율 : 70%)
        gbc.gridy = 1;
        gbc.weighty = 0.7;
        calculatorPanel.add(lowerWrapper, gbc);

        // 메인 패널 생성 (BorderLayout.CENTER에 추가될 패널)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.BLACK);

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.CENTER;
        mainGbc.fill = GridBagConstraints.NONE;

        mainPanel.add(calculatorPanel, mainGbc);

        // JFrame에 mainPanel 추가
        add(mainPanel, BorderLayout.CENTER);
    }

    //  설정 초기화
    private void finalizeSetup() {
        //  초기 크기 설정 (For debug)
        setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setVisible(true);

        //  초기 비율 유지
        maintainAspectRatio();
    }

    JavaHW3() {
        init();

        setUpperWrapper();
        setLowerWrapper();

        setLayout();
        finalizeSetup();

        //  창 크기 변경 시 비율 유지
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                maintainAspectRatio();
            }
        });
    }

    public static void main(String[] args) {
        new JavaHW3();
    }
}
