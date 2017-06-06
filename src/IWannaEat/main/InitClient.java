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
		setTitle("���� ���� �԰� �ʹ�.");
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
			// ip, port�� ���ڷ� Socket�� ��ü�� ����
			// ����� ȯ�濡 ���� ip�� port���� �����ؼ� ���
			socket = new Socket("127.0.0.1", 8080);
			// �������κ��� �Է½�Ʈ���� ȹ��
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			// �������κ��� ��½�Ʈ���� ȹ��
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
        
        getContentPane().add("Login", new LoginPane(this, socket));
        getContentPane().add("Signin", new SigninPane(this, socket));
        
        setVisible(true);
		System.out.println("�α��� Ŭ���̾�Ʈ ���� �Ϸ�");
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
