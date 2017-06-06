package IWannaEat.main;

import java.awt.CardLayout;

import javax.swing.*;

import IWannaEat.Panel.init.LoginPane;
import IWannaEat.Panel.init.SigninPane;

public class InitClient extends JFrame{
	private CardLayout cards = new CardLayout();
	private static InitClient inc;
	
	public InitClient() {
		setTitle("���� ���� �԰� �ʹ�.");
		setSize(500, 700);
        getContentPane().setLayout(cards);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        getContentPane().add("Login", new LoginPane(this));
        getContentPane().add("Signin", new SigninPane(this));
        
        setVisible(true);
		System.out.println("�α��� Ŭ���̾�Ʈ ���� �Ϸ�");
	}
	
	public CardLayout getCardLayout() {
		 return cards;
	}
	
	public static void main(String arts[]){
		inc = new InitClient();
	}
}
