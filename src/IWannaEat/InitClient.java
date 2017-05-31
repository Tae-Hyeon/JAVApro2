package IWannaEat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.*;

class LoginClient extends JFrame{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	// Panel을 담는 Container 선언
	private Container ct;

	// Login시  ID, Password 입력을 위한 JTextField 
	private JTextField ltextfield;
	private JPasswordField lpasswordfield;
	
	// Login시 클릭할 로그인, 종료 버튼 
	private JButton lbutton1;
	private JButton lbutton2;
	private JButton lbutton3;
	
	// JRadioButton, JTextField, JButton, JLabel을 담는 일종의 그릇인 JPanel 선언 
	private JPanel lpanel1;
	private JPanel lpanel2;
	private JPanel lpanel3;
	
	private JLabel llabel1;
	private JLabel llabel2;
	
	public LoginClient(){
		System.out.println("로그인 클라이언트 생성자 실행...");
		// Container 생성, 상단 - label, 중단 - ID, Password입력, 하단 - 버튼 layout 생성
		ct = getContentPane();
		ct.setLayout(new BorderLayout(20,20));
		
		// JTextField에 나타내는 값 설정
		ltextfield = new JTextField("ID");
		lpasswordfield = new JPasswordField("Password");
		
		lbutton1 = new JButton("로그인");
		lbutton1 = new JButton("회원가입");
		lbutton2 = new JButton("종료");
		
		llabel1 = new JLabel();
		llabel2 = new JLabel();
		llabel1.setText("나는 밥이 먹고 싶다!!");
		llabel2.setText("Login을 해주세요");
		
		// 상단 panel label 추가
		lpanel1 = new JPanel();
		lpanel1.setLayout(new GridLayout(2,1,0,10));
		lpanel1.add(llabel1);
		lpanel1.add(llabel2);
		
		// 중단 panel ID, Password 입력 필드 추가	
		lpanel2 = new JPanel();
		lpanel2.setLayout(new GridLayout(3,1,0,20));
		lpanel2.add(ltextfield);
		lpanel2.add(lpasswordfield);
		
		// 하단 panel 버튼 추가
		lpanel3 = new JPanel();
		lpanel3.setLayout(new GridLayout(1,2,20,0));
		lpanel3.add(lbutton1);
		lpanel3.add(lbutton2);
		lpanel3.add(lbutton3);
		
		// container에 panel추가
		ct.add(lpanel1,BorderLayout.NORTH);
		ct.add(lpanel2,BorderLayout.CENTER);
		ct.add(lpanel3,BorderLayout.SOUTH);
		
		// GUI상세조정
		setTitle("나는 밥이 먹고 싶다");
		setSize(500, 700);

		//윈도우 창의 X버튼을 누를시에 해당되는 이벤트 설정
		//이벤트를 설정하지 않을시에 소켓전송이 비정상적으로 
		//종료되기 때문에 예외처리가 발생
		//따라서 이벤트에 의해서 stop() 메소드를 호출
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		try {
			// ip, port를 인자로 Socket형 객체를 생성
			// 사용자 환경에 따라 ip와 port값을 변경해서 사용
			socket = new Socket("127.0.0.1", 8080);
			// 소켓으로부터 입력스트림을 획득
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			// 소켓으로부터 출력스트림을 획득
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
	}
	/*
	 * method : actionPerformed() 
	 * 이벤트 처리를 위한 메소드 
	 * 회원가입 버튼을 누르면 회원가입 페이지로 변경 
	 * 종료 버튼을 누르면 종료
	 * 로그인 버튼을 누르면 '|'를 구분자로 해서 사용자의 정보를 서버측에 전달
	 */

	public void actionPerformed(ActionEvent event) {
		String message = null;
		String success = null;
		String Type = null;
		
		String Action = null;
		String ID = null;
		String Password = null;
		
		//ID, Password를 변수에 대입하는 제어문
		if (event.getActionCommand().equals("로그인")) {
			if(ltextfield.getText() != null && lpasswordfield.getPassword() != null){
				Action = event.getActionCommand();
				ID = ltextfield.getText();
				Password = new String(this.lpasswordfield.getPassword());
			
				try {
					// 출력스트림에 회원정보를 “|”를 구분자로 출력
					dataOut.writeUTF(Action + "|" + ID + "|" + Password); 
					
					// 출력스트림의 버퍼를 비워 바로 전송
					dataOut.flush();
				} catch (Exception e) {
					// 예외가 발생하면 예외의 상태를 콘솔에 표시
					System.out.println("Error : " + e.toString());
				}
			}
		//	else if(ID == null && Password != null ) {
		//		llabel2.setText("ID를 입력해 주세요");
		//	}
		//	else if(ID != null && Password == null) {
		//		llabel2.setText("Password를 입력해 주세요");
		//	}
		//	else{
		//		llabel2.setText("회원 정보를 입력해 주세요");
		//	}
			
			try {
				message = dataIn.readUTF();
				StringTokenizer stk = new StringTokenizer(message, "|");
				Type = stk.nextToken(); 
				success = stk.nextToken();
				
				System.out.println("로그인 : " + success);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(success.equals("success")){
				if (Type.equals("client")){
					//TODO: 로그인 완료 -- 손님 객체 생성
				}
				else if (Type.equals("restaurant")){
					//TODO: 로그인 완료 -- 가게 객체 생성
				}
			}
			else if (success.equals("fail")){
				//TODO: 실패
			}
		}
		
		// 발생한 ActionEvent의 ActionCommnad(여기서는 버튼의 text)가 		"Submit"가 같으면 실행
		if (event.getActionCommand().equals("회원가입")) {
			//TODO: 회원가입 전환
		}
	}	
	public void stop(){
		try {
			dataIn.close();
			dataOut.close();
			socket.close();

		} catch (IOException e) {
			System.out.println("Error : " + e.toString());
		}
	}
	//abstract class MyActionListener implements ActionListener{
	//	public void changePane(ActionEvent e){
	//		win.change("SigninClient");
	//	}
	//}
}


class SigninClient extends JFrame implements ActionListener{
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	// Panel을 담는 Container 선언
	private Container sct;
	
	// Sign in시  Guest, Restaurant JRadioButton들을 그룹화 하기위한 ButtonGroup
	private ButtonGroup sjbg;
		
	// Sign in시 Guest, Restaurant 버튼
	private JRadioButton sjrb1;
	private JRadioButton sjrb2;
		
	// Sign in시 ID, Password, Name, Phone Number 입력을 위한 JTextField
	private JTextField stextfield1;
	private JTextField stextfield2;
	private JTextField stextfield3;
	private JPasswordField spasswordfield1;
	private JPasswordField spasswordfield2;
		
	// Sign in시 클릭할 회원가입, 취소버튼
	private JButton sbutton1;
	private JButton sbutton2;
		
	// JRadioButton, JTextField, JButton, JLabel을 담는 일종의 그릇인 JPanel 선언 
	private JPanel spanel1;
	private JPanel spanel2;
	private JPanel spanel3;
	private JPanel spanel4;
	
	private JLabel slabel1;
	private JLabel slabel2;
		
	public SigninClient(){
	// Container 생성, 상단 - label, 중단 - RadioButton, ID, Password입력, 하단 - 버튼 layout 생성
		sct = getContentPane();
		sct.setLayout(new BorderLayout(20,20));	
		
	//ButtonGroup의 경우 물리적 객체(눈에보이는)가 아닌 논리적 객체
		sjbg = new ButtonGroup();
		
	//각각의 JRadioButton을 생성하고 처음에 선택되어 있지 않도록 false로 선언
		sjrb1 = new JRadioButton("Guest", false);
		sjrb2 = new JRadioButton("Restaurant", false);
		
	//각각의 JRadioButton을 ButtonGroup에 그룹화
		sjbg.add(sjrb1);
		sjbg.add(sjrb2);	
		
	//JTextField에 나타내는 값 설정
		stextfield1 = new JTextField("ID");
		stextfield2 = new JTextField("Name");
		stextfield3 = new JTextField("Phone Number");
		spasswordfield1 = new JPasswordField("Password");
		spasswordfield2 = new JPasswordField("Retype Password");
		
		sbutton1 = new JButton("회원가입");
		sbutton1.addActionListener(this);
		sbutton2 = new JButton("취소");
		
		
		slabel1 = new JLabel();
		slabel2 = new JLabel();
		slabel1.setText("나는 밥이 먹고 싶다!!");
		slabel2.setText("회원가입을 해주세요");
		
	// 상단 panel label 추가
		spanel1 = new JPanel();
		spanel1.setLayout(new GridLayout(2,1,0,10));
		spanel1.add(slabel1);
		spanel1.add(slabel2);
		
	// 중단 panel 속 라디오버튼을 위한 panel;
		spanel2 = new JPanel();
		spanel2.setLayout(new GridLayout(1,2,10,0));
		spanel2.add(sjrb1);
		spanel2.add(sjrb2);
		
	// 중단 panel ID, Password 입력 필드 추가	
		spanel3 = new JPanel();
		spanel3.setLayout(new GridLayout(6,1,0, 10));
		spanel3.add(spanel2);
		spanel3.add(stextfield1);
		spanel3.add(spasswordfield1);
		spanel3.add(spasswordfield2);
		spanel3.add(stextfield2);
		spanel3.add(stextfield3);
		
	// 하단 panel 버튼 추가
		spanel4 = new JPanel();
		spanel4.setLayout(new GridLayout(1,2,20,0));
		spanel4.add(sbutton1);
		spanel4.add(sbutton2);
		
	// container에 panel추가
		sct.add(spanel1,BorderLayout.NORTH);
		sct.add(spanel3,BorderLayout.CENTER);
		sct.add(spanel4,BorderLayout.SOUTH);
		
		// GUI상세조정
		setTitle("나는 밥이 먹고 싶다");
		setSize(500, 700);

		//윈도우 창의 X버튼을 누를시에 해당되는 이벤트 설정
		//이벤트를 설정하지 않을시에 소켓전송이 비정상적으로 
		//종료되기 때문에 예외처리가 발생
		//따라서 이벤트에 의해서 stop() 메소드를 호출
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		try {
			// ip, port를 인자로 Socket형 객체를 생성
			// 사용자 환경에 따라 ip와 port값을 변경해서 사용
			socket = new Socket("127.0.0.1", 8080);
			// 소켓으로부터 입력스트림을 획득
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			// 소켓으로부터 출력스트림을 획득
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
	}
	public void actionPerformed(ActionEvent event) {
		String success = null;
		
		String Action = null;
		String Type = null;
		String ID = null;
		String Password = null;
		String Retype_Passwd = null;
		String Name = null;
		String Phone = null;
		
		//ID, Password를 변수에 대입하는 제어문
		if (event.getActionCommand().equals("회원가입")) {
			Action = event.getActionCommand();
			Type = (sjrb1.isSelected()) ? sjrb1.getText() : sjrb2.getText();
			ID = stextfield1.getText();
			Password = new String(this.spasswordfield1.getPassword());
			Retype_Passwd = new String(this.spasswordfield2.getPassword());
			Name = stextfield2.getText();
			Phone = stextfield3.getText();
			if(!(Password.equals(Retype_Passwd))){
				System.out.println(Password + " : " + Retype_Passwd);
				System.out.println("패스워드 불일치");
			}
			else if(Type != null && ID != null && Password != null && Password.equals(Retype_Passwd) && Name != null && Phone != null){
				try {
					// 출력스트림에 회원정보를 “|”를 구분자로 출력
					dataOut.writeUTF(Action + "|" + Type + "|" + ID + "|" + Password + "|" + Name + "|" + Phone); 
					System.out.println((Action + "|" + Type + "|" + ID + "|" + Password + "|" + Name + "|" + Phone + "  daterout..."));
					// 출력스트림의 버퍼를 비워 바로 전송
					dataOut.flush();
				} catch (Exception e) {
					// 예외가 발생하면 예외의 상태를 콘솔에 표시
					System.out.println("Error : " + e.toString());
				}
			} 
			else{
				System.out.println("회원정보를 모두 입력해 주세요!");
			}
			
			try {
				success = dataIn.readUTF();
				System.out.println("회원가입 : " + success);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(success.equals("success")){
				//TODO: 회원가입 후 로그인 돌아가기
			}
			else if (success.equals("fail")){
				//TODO: 실패
			}
		}
		
		// 발생한 ActionEvent의 ActionCommnad(여기서는 버튼의 text)가 		"Submit"가 같으면 실행
		if (event.getActionCommand().equals("취소")) {
			//TODO: 로그인 창으로 전환
		}
	}	
	public void stop(){
		try {
			dataIn.close();
			dataOut.close();
			socket.close();

		} catch (IOException e) {
			System.out.println("Error : " + e.toString());
		}
	}
}

public class InitClient extends JFrame{
	
	private static InitClient inc;
	
	public InitClient() {
		SigninClient sc= new SigninClient();
		System.out.println("로그인 클라이언트 생성 완료");
	}
	public static void main(String arts[]){
		inc = new InitClient();
	}
}
