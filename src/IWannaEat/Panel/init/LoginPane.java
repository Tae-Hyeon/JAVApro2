package IWannaEat.Panel.init;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import IWannaEat.Panel.guest.Select;
import IWannaEat.Panel.restaurant.FirstSet;
import IWannaEat.Panel.restaurant.RestaurantPane;
import IWannaEat.Panel.restaurant.SetOption;
import IWannaEat.main.InitClient;

public class LoginPane extends JPanel implements ActionListener{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;

	// Panel을 담을 mainpanel
	private JPanel lmp;
	
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
	
	public LoginPane(InitClient init, Socket socket){
		Init = init;
		System.out.println("로그인 클라이언트 생성자 실행...");
		// main Panel 생성, 상단 - label, 중단 - ID, Password입력, 하단 - 버튼 layout 생성
		lmp = new JPanel();
		lmp.setLayout(new BorderLayout(20,20));
		
		// JTextField에 나타내는 값 설정
		ltextfield = new JTextField("ID");
		lpasswordfield = new JPasswordField("Password");
		
		lbutton1 = new JButton("로그인");
		lbutton1.addActionListener(this);
		lbutton2 = new JButton("회원가입");
		lbutton2.addActionListener(this);
		lbutton3 = new JButton("종료");
		lbutton3.addActionListener(this);
		
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
		lpanel3.setLayout(new GridLayout(1,3,20,0));
		lpanel3.add(lbutton1);
		lpanel3.add(lbutton2);
		lpanel3.add(lbutton3);
		
		// main Panel에 panel추가
		lmp.add(lpanel1,BorderLayout.NORTH);
		lmp.add(lpanel2,BorderLayout.CENTER);
		lmp.add(lpanel3,BorderLayout.SOUTH);
		
		add(lmp);
		// GUI상세조정
		setSize(500, 700);

		setVisible(true);
		
		try {
			// ip, port를 인자로 Socket형 객체를 생성
			// 사용자 환경에 따라 ip와 port값을 변경해서 사용
			this.socket = socket;
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
		String isSet = null;
		
		String Action = null;
		String ID = null;
		String Password = null;
		
		//ID, Password를 변수에 대입하는 제어문 --> 함수로 추상화?? 해야함
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
				success = stk.nextToken();
					
				System.out.println("로그인 : " + success);
				if(success.equals("success")){
					Type = stk.nextToken(); 
					ID = stk.nextToken();
					if (Type.equals("Guest")){
						//TODO: 로그인 완료 -- 손님 객체 생성 cardLayout에 추가
						System.out.println("guest panel 생성...");
						Init.getContentPane().add("Select", new Select(Init, socket));
						Init.getCardLayout().show(Init.getContentPane(), "Select");
					}
					else if (Type.equals("Restaurant")){
						//TODO: 로그인 완료 -- 가게 객체 생성  xx option이 있는지 없는지도 스트림으로 받아서 그것에 따라 생성 처리
						isSet = stk.nextToken();
						if(isSet.equals("setted")){
							Init.getContentPane().add("SetOption", new SetOption(Init, socket));
							Init.getContentPane().add("Restaurant", new RestaurantPane(Init, socket));
							Init.getCardLayout().show(Init.getContentPane(), "Restaurant");
						}
						else if(isSet.equals("not setted")){
							Init.getContentPane().add("FirstSet", new FirstSet(Init, socket));
							Init.getCardLayout().show(Init.getContentPane(), "FirstSet");
						}
					}
				}
				else if (success.equals("fail")){
					//TODO: 실패
					llabel2.setText("비밀번호를 확인해 주세요.");
				}
				else if (success.equals("none")){
					//id가 없을 경우
					llabel2.setText("회원가입을 해주세요!!");
				}
			}catch (IOException e){
				System.out.println("Error : " + e.toString());
			}
		}
		
		// 발생한 ActionEvent의 ActionCommnad(여기서는 버튼의 text)가 		"회원가입"가 같으면 실행
		else if (event.getActionCommand().equals("회원가입")) {
			//TODO: 회원가입 전환
			System.out.println("회원가입 누름");
			Init.getCardLayout().show(Init.getContentPane(), "Signin");
		}
		else if (event.getActionCommand().equals("종료")) {
			//TODO: 회원가입 전환
			stop();
			Init.dispose();
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
