package ps;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

/**
 * class : PizzaClient
 * JFrame을 상속받고 , ActionListener 인터페이스를 통한 다중상속의 성격을 이용
 * 애플릿이 아닌 일반 응용프로그램이기 때문에 시작하기 위해서는 main()메소드를 호출하여 시작. 
 */

public class PizzaClient extends JFrame implements ActionListener {
	//size 및 taste 관련 JRadioButton들을 그룹화 하기위한 ButtonGroup 선언
	//ButtonGroup 속의 RadionButton은 하나만 선택 가능
	private ButtonGroup jbg1;
	private ButtonGroup jbg2;

	//각각의 선택을 위한 JRadioButton 선언
	private JRadioButton jrb1;
	private JRadioButton jrb2;
	private JRadioButton jrb3;
	private JRadioButton jrb4;
	private JRadioButton jrb5;
	private JRadioButton jrb6;

	//클릭버튼을 하기 위한 JButton 선언
	private JButton button1;

	//JRadioButton, JTextField, JButton, JLabel을 담는 일종의 그릇인 JPanel 선언
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	private JPanel panel5;

	//Name, Address, Phone Number를 입력하기 위한 JTextField 선언
	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;

	private JLabel label1;

	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private static PizzaClient pca;
	
	//선택된 라디오버튼의 text값을 넣기 저장하기 위한 String 변수 선언
	private String size;
	private String taste;

	public PizzaClient() {

		//ButtonGroup의 경우 물리적 객체(눈에보이는)가 아닌 논리적 객체
		jbg1 = new ButtonGroup();
		jbg2 = new ButtonGroup();

		//각각의 JRadioButton을 생성하고 처음에 선택되어 있지 않도록 false로 선언
		jrb1 = new JRadioButton("Small", false);
		jrb2 = new JRadioButton("Medium", false);
		jrb3 = new JRadioButton("Large", false);
		jrb4 = new JRadioButton("Veggies", false);
		jrb5 = new JRadioButton("Meat", false);
		jrb6 = new JRadioButton("California", false);

		//각각의 JRadioButton을 ButtonGroup에 그룹화
		jbg1.add(jrb1);
		jbg1.add(jrb2);
		jbg1.add(jrb3);

		jbg2.add(jrb4);
		jbg2.add(jrb5);
		jbg2.add(jrb6);

		//JTextField에 나타내는 값 설정
		textField1 = new JTextField("Name");
		textField2 = new JTextField("Address");
		textField3 = new JTextField("Phone Number");

		//JTextField의 폰트 설정
		//Font 클래스의 생성자는 font이름, font 스타일, font 크기 순으로 설정
		//참고 : font 이름에는 Monospaced, Serif, SansSerif 등이 존재
		//참고 : font 스타일에는 BOLD, ITALIC 등이 존재
		textField1.setFont(new Font("SansSerif", Font.BOLD, 12));
		textField2.setFont(new Font("SansSerif", Font.BOLD, 12));
		textField3.setFont(new Font("SansSerif", Font.BOLD, 12));

		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		panel5 = new JPanel();

		button1 = new JButton();

		label1 = new JLabel();

		//panel1의 레이아웃을 설정하고 panel1안에서의 간격은 10,10으로 설정
		panel1.setLayout(new BorderLayout(10,10));
		panel1.add(textField1, BorderLayout.NORTH);
		panel1.add(textField2, BorderLayout.CENTER);
		panel1.add(textField3, BorderLayout.SOUTH);
		add(panel1, BorderLayout.NORTH);

		//panel2의 레이아웃을 설정하고 panel4와 panel5를 panel2안에 포함
		panel2.setLayout(new GridLayout());
		panel4.setLayout(new GridLayout(3, 1));
		panel4.setBackground(new Color(204, 204, 255));
		panel2.add(panel4);
		panel5.setLayout(new GridLayout(3, 1));
		panel5.setBackground(new Color(204, 204, 255));
		panel2.add(panel5);

		//panel4에 size에 관련된 JRadioButton 과 JTextField 포함
		//ButtonGroup은 논리적 객체이기 때문에 포함 하지 않아도 됨
		panel4.add(jrb1);
		panel4.add(jrb2);
		panel4.add(jrb3);


		//panel5에 taste에 관련된 JRadioButton 과 JTextField 포함
		//ButtonGroup은 논리적 객체이기 때문에 포함 하지 않아도 됨
		panel5.add(jrb4);
		panel5.add(jrb5);
		panel5.add(jrb6);

		add(panel2, BorderLayout.CENTER);
		panel3.setLayout(new BorderLayout());

		//button1.setLabel 이라고도 표현 가능하나 
		//jdk 5.0부터 추천하지 않는 메소드이기 때문에 button1.setText 로 대체
		button1.setText("Submit");

		//button1을 클릭할때 발생하는 이벤트를 addActtionListener에 추가
		button1.addActionListener(this);
		panel3.add(button1, BorderLayout.CENTER);

		label1.setBackground(new Color(153, 153, 255));
		label1.setText("Information");
		panel3.add(label1, BorderLayout.SOUTH);
		add(panel3, BorderLayout.SOUTH);

		try {
			// ip, port를 인자로 Socket형 객체를 생성
			// 사용자 환경에 따라 ip와 port값을 변경해서 사용
			socket = new Socket("127.0.0.1", 8080);
			// 소켓으로부터 입력스트림을 획득
		dataIn = new DataInputStream(new BufferedInputStream(socket
					.getInputStream()));
			// 소켓으로부터 출력스트림을 획득
		dataOut = new DataOutputStream(new BufferedOutputStream(socket
					.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
	}

	
	/*
	 * method : actionPerformed() 
	 * 이벤트 처리를 위한 메소드 
	 * submit 버튼을 누루게되면 label1(상태라벨)을 'Sending Order...'로 바꾼후 
	 * '|'를 구분자로 해서 사용자의 정보와 주문정보를 서버측에전달
	 *label1을 'Thank you!'로 바꿔서 주문의 완료를 알림
	 */

	public void actionPerformed(ActionEvent event) {
		//jrb1~jrb3중에서 선택된 버튼의 Text값을 size 변수에 대입 위한 제어문
		if(jrb1.isSelected()){
			size = jrb1.getText();
		}
		else if(jrb2.isSelected()){
			size = jrb2.getText();
		}
		else if(jrb3.isSelected()){
			size = jrb3.getText();
		}
		//jrb4~jrb6중에서 선택된 버튼의 Text값을 taste 변수에 대입 위한 제어문
		if(jrb4.isSelected()){
			taste = jrb4.getText();
		}
		else if(jrb5.isSelected()){
			taste = jrb5.getText();
		}
		else if(jrb6.isSelected()){
			taste = jrb6.getText();
		}
		
		// 발생한 ActionEvent의 ActionCommnad(여기서는 버튼의 text)가 		"Submit"가 같으면 실행
		if (event.getActionCommand().equals("Submit")) {
			label1.setText("Sending Order...");
			try {
				// 출력스트림에 주문정보를 “|”를 구분자로 출력
				dataOut.writeUTF(textField1.getText() + "|"
				+ textField2.getText() + "|" 
				+ textField3.getText() + "|" + size + "|"+ taste);
				// 주문이 완료되었음을 레이블의 
				//텍스트 값을 변경해서 알림
				label1.setText("Thank you!");
				// 출력스트림의 버퍼를 비워 바로 전송
				dataOut.flush();
			} catch (Exception e) {
				// 예외가 발생하면 예외의 상태를 레이블에 표시
				label1.setText("Error : " + e.toString());
			}
		}
	}

	/*
	 * method : stop() 
	 * 모든 연결을 끊고 소켓을 닫음 
	 * 위에서 IOException을 처리하기 위해서 호출 가능
	 */
	public void stop() {
		try {
			dataIn.close();
			dataOut.close();
			socket.close();

		} catch (IOException e) {
			label1.setText("Error : " + e.toString());
		}
	}

	public static void main(String[] args) {
		pca = new PizzaClient();
		pca.setTitle("Pizza");
		pca.setSize(200, 350);

		//윈도우 창의 X버튼을 누를시에 해당되는 이벤트 설정
		//이벤트를 설정하지 않을시에 소켓전송이 비정상적으로 
		//종료되기 때문에 예외처리가 발생
		//따라서 이벤트에 의해서 stop() 메소드를 호출
		pca.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pca.stop();
			}
		});
		pca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pca.setVisible(true);

	}
}