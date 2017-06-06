package IWannaEat.main;

import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

import IWannaEat.Panel.init.LoginPane;
import IWannaEat.Panel.init.SigninPane;

public class InitClient extends JFrame{
	private CardLayout cards = new CardLayout();
	private static InitClient inc;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	public InitClient() {
		setTitle("나는 밥이 먹고 싶다.");
		setSize(500, 700);
        getContentPane().setLayout(cards);
        this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				inc.stop();
			}
		});
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
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
        
        getContentPane().add("Login", new LoginPane(this, socket));
        getContentPane().add("Signin", new SigninPane(this, socket));
        
        setVisible(true);
		System.out.println("로그인 클라이언트 생성 완료");
	}
	
	public CardLayout getCardLayout() {
		 return cards;
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
	
	public static void main(String arts[]){
		inc = new InitClient();
	}
}
