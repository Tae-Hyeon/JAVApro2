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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import IWannaEat.main.InitClient;

public class SigninPane extends JPanel implements ActionListener{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	// Panel 을 담을 mainpanel
	private JPanel smp;
	
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
		
	public SigninPane(InitClient init){
		Init = init;
	// main Panel 생성, 상단 - label, 중단 - RadioButton, ID, Password입력, 하단 - 버튼 layout 생성
		smp = new JPanel();
		smp.setLayout(new BorderLayout(20,20));	
		
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
		sbutton2.addActionListener(this);
		
		
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
		
	// main Panel에 panel추가
		smp.add(spanel1,BorderLayout.NORTH);
		smp.add(spanel3,BorderLayout.CENTER);
		smp.add(spanel4,BorderLayout.SOUTH);
		
		add(smp);
		// GUI상세조정
		
		setSize(500, 700);
	
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
		String success = "fail";
		
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
					success = "success";
				} catch (Exception e) {
					// 예외가 발생하면 예외의 상태를 콘솔에 표시
					System.out.println("Error : " + e.toString());
				}
			} 
			else{
				System.out.println("회원정보를 모두 입력해 주세요!");
			}
			
			try {
				System.out.println("data in");
				if (success == "success")
					success = dataIn.readUTF();
				System.out.println("회원가입 : " + success);
			} catch (IOException e) {  
				e.printStackTrace();
			}
			if(success.equals("success")){
				System.out.println("로그인 :SUCCESS");
				Init.getCardLayout().show(Init.getContentPane(), "Login");
			}
			else if (success.equals("fail")){
				//TODO: 실패
			}
		}
		
		// 발생한 ActionEvent의 ActionCommnad(여기서는 버튼의 text)가 		"Submit"가 같으면 실행
		if (event.getActionCommand().equals("취소")) {
			Init.getCardLayout().show(Init.getContentPane(), "Login");
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
