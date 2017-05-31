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
	
	// Panel�� ��� Container ����
	private Container ct;

	// Login��  ID, Password �Է��� ���� JTextField 
	private JTextField ltextfield;
	private JPasswordField lpasswordfield;
	
	// Login�� Ŭ���� �α���, ���� ��ư 
	private JButton lbutton1;
	private JButton lbutton2;
	private JButton lbutton3;
	
	// JRadioButton, JTextField, JButton, JLabel�� ��� ������ �׸��� JPanel ���� 
	private JPanel lpanel1;
	private JPanel lpanel2;
	private JPanel lpanel3;
	
	private JLabel llabel1;
	private JLabel llabel2;
	
	public LoginClient(){
		System.out.println("�α��� Ŭ���̾�Ʈ ������ ����...");
		// Container ����, ��� - label, �ߴ� - ID, Password�Է�, �ϴ� - ��ư layout ����
		ct = getContentPane();
		ct.setLayout(new BorderLayout(20,20));
		
		// JTextField�� ��Ÿ���� �� ����
		ltextfield = new JTextField("ID");
		lpasswordfield = new JPasswordField("Password");
		
		lbutton1 = new JButton("�α���");
		lbutton1 = new JButton("ȸ������");
		lbutton2 = new JButton("����");
		
		llabel1 = new JLabel();
		llabel2 = new JLabel();
		llabel1.setText("���� ���� �԰� �ʹ�!!");
		llabel2.setText("Login�� ���ּ���");
		
		// ��� panel label �߰�
		lpanel1 = new JPanel();
		lpanel1.setLayout(new GridLayout(2,1,0,10));
		lpanel1.add(llabel1);
		lpanel1.add(llabel2);
		
		// �ߴ� panel ID, Password �Է� �ʵ� �߰�	
		lpanel2 = new JPanel();
		lpanel2.setLayout(new GridLayout(3,1,0,20));
		lpanel2.add(ltextfield);
		lpanel2.add(lpasswordfield);
		
		// �ϴ� panel ��ư �߰�
		lpanel3 = new JPanel();
		lpanel3.setLayout(new GridLayout(1,2,20,0));
		lpanel3.add(lbutton1);
		lpanel3.add(lbutton2);
		lpanel3.add(lbutton3);
		
		// container�� panel�߰�
		ct.add(lpanel1,BorderLayout.NORTH);
		ct.add(lpanel2,BorderLayout.CENTER);
		ct.add(lpanel3,BorderLayout.SOUTH);
		
		// GUI������
		setTitle("���� ���� �԰� �ʹ�");
		setSize(500, 700);

		//������ â�� X��ư�� �����ÿ� �ش�Ǵ� �̺�Ʈ ����
		//�̺�Ʈ�� �������� �����ÿ� ���������� ������������ 
		//����Ǳ� ������ ����ó���� �߻�
		//���� �̺�Ʈ�� ���ؼ� stop() �޼ҵ带 ȣ��
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
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
	}
	/*
	 * method : actionPerformed() 
	 * �̺�Ʈ ó���� ���� �޼ҵ� 
	 * ȸ������ ��ư�� ������ ȸ������ �������� ���� 
	 * ���� ��ư�� ������ ����
	 * �α��� ��ư�� ������ '|'�� �����ڷ� �ؼ� ������� ������ �������� ����
	 */

	public void actionPerformed(ActionEvent event) {
		String message = null;
		String success = null;
		String Type = null;
		
		String Action = null;
		String ID = null;
		String Password = null;
		
		//ID, Password�� ������ �����ϴ� ���
		if (event.getActionCommand().equals("�α���")) {
			if(ltextfield.getText() != null && lpasswordfield.getPassword() != null){
				Action = event.getActionCommand();
				ID = ltextfield.getText();
				Password = new String(this.lpasswordfield.getPassword());
			
				try {
					// ��½�Ʈ���� ȸ�������� ��|���� �����ڷ� ���
					dataOut.writeUTF(Action + "|" + ID + "|" + Password); 
					
					// ��½�Ʈ���� ���۸� ��� �ٷ� ����
					dataOut.flush();
				} catch (Exception e) {
					// ���ܰ� �߻��ϸ� ������ ���¸� �ֿܼ� ǥ��
					System.out.println("Error : " + e.toString());
				}
			}
		//	else if(ID == null && Password != null ) {
		//		llabel2.setText("ID�� �Է��� �ּ���");
		//	}
		//	else if(ID != null && Password == null) {
		//		llabel2.setText("Password�� �Է��� �ּ���");
		//	}
		//	else{
		//		llabel2.setText("ȸ�� ������ �Է��� �ּ���");
		//	}
			
			try {
				message = dataIn.readUTF();
				StringTokenizer stk = new StringTokenizer(message, "|");
				Type = stk.nextToken(); 
				success = stk.nextToken();
				
				System.out.println("�α��� : " + success);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(success.equals("success")){
				if (Type.equals("client")){
					//TODO: �α��� �Ϸ� -- �մ� ��ü ����
				}
				else if (Type.equals("restaurant")){
					//TODO: �α��� �Ϸ� -- ���� ��ü ����
				}
			}
			else if (success.equals("fail")){
				//TODO: ����
			}
		}
		
		// �߻��� ActionEvent�� ActionCommnad(���⼭�� ��ư�� text)�� 		"Submit"�� ������ ����
		if (event.getActionCommand().equals("ȸ������")) {
			//TODO: ȸ������ ��ȯ
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
	
	// Panel�� ��� Container ����
	private Container sct;
	
	// Sign in��  Guest, Restaurant JRadioButton���� �׷�ȭ �ϱ����� ButtonGroup
	private ButtonGroup sjbg;
		
	// Sign in�� Guest, Restaurant ��ư
	private JRadioButton sjrb1;
	private JRadioButton sjrb2;
		
	// Sign in�� ID, Password, Name, Phone Number �Է��� ���� JTextField
	private JTextField stextfield1;
	private JTextField stextfield2;
	private JTextField stextfield3;
	private JPasswordField spasswordfield1;
	private JPasswordField spasswordfield2;
		
	// Sign in�� Ŭ���� ȸ������, ��ҹ�ư
	private JButton sbutton1;
	private JButton sbutton2;
		
	// JRadioButton, JTextField, JButton, JLabel�� ��� ������ �׸��� JPanel ���� 
	private JPanel spanel1;
	private JPanel spanel2;
	private JPanel spanel3;
	private JPanel spanel4;
	
	private JLabel slabel1;
	private JLabel slabel2;
		
	public SigninClient(){
	// Container ����, ��� - label, �ߴ� - RadioButton, ID, Password�Է�, �ϴ� - ��ư layout ����
		sct = getContentPane();
		sct.setLayout(new BorderLayout(20,20));	
		
	//ButtonGroup�� ��� ������ ��ü(�������̴�)�� �ƴ� ���� ��ü
		sjbg = new ButtonGroup();
		
	//������ JRadioButton�� �����ϰ� ó���� ���õǾ� ���� �ʵ��� false�� ����
		sjrb1 = new JRadioButton("Guest", false);
		sjrb2 = new JRadioButton("Restaurant", false);
		
	//������ JRadioButton�� ButtonGroup�� �׷�ȭ
		sjbg.add(sjrb1);
		sjbg.add(sjrb2);	
		
	//JTextField�� ��Ÿ���� �� ����
		stextfield1 = new JTextField("ID");
		stextfield2 = new JTextField("Name");
		stextfield3 = new JTextField("Phone Number");
		spasswordfield1 = new JPasswordField("Password");
		spasswordfield2 = new JPasswordField("Retype Password");
		
		sbutton1 = new JButton("ȸ������");
		sbutton1.addActionListener(this);
		sbutton2 = new JButton("���");
		
		
		slabel1 = new JLabel();
		slabel2 = new JLabel();
		slabel1.setText("���� ���� �԰� �ʹ�!!");
		slabel2.setText("ȸ�������� ���ּ���");
		
	// ��� panel label �߰�
		spanel1 = new JPanel();
		spanel1.setLayout(new GridLayout(2,1,0,10));
		spanel1.add(slabel1);
		spanel1.add(slabel2);
		
	// �ߴ� panel �� ������ư�� ���� panel;
		spanel2 = new JPanel();
		spanel2.setLayout(new GridLayout(1,2,10,0));
		spanel2.add(sjrb1);
		spanel2.add(sjrb2);
		
	// �ߴ� panel ID, Password �Է� �ʵ� �߰�	
		spanel3 = new JPanel();
		spanel3.setLayout(new GridLayout(6,1,0, 10));
		spanel3.add(spanel2);
		spanel3.add(stextfield1);
		spanel3.add(spasswordfield1);
		spanel3.add(spasswordfield2);
		spanel3.add(stextfield2);
		spanel3.add(stextfield3);
		
	// �ϴ� panel ��ư �߰�
		spanel4 = new JPanel();
		spanel4.setLayout(new GridLayout(1,2,20,0));
		spanel4.add(sbutton1);
		spanel4.add(sbutton2);
		
	// container�� panel�߰�
		sct.add(spanel1,BorderLayout.NORTH);
		sct.add(spanel3,BorderLayout.CENTER);
		sct.add(spanel4,BorderLayout.SOUTH);
		
		// GUI������
		setTitle("���� ���� �԰� �ʹ�");
		setSize(500, 700);

		//������ â�� X��ư�� �����ÿ� �ش�Ǵ� �̺�Ʈ ����
		//�̺�Ʈ�� �������� �����ÿ� ���������� ������������ 
		//����Ǳ� ������ ����ó���� �߻�
		//���� �̺�Ʈ�� ���ؼ� stop() �޼ҵ带 ȣ��
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
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
		
		//ID, Password�� ������ �����ϴ� ���
		if (event.getActionCommand().equals("ȸ������")) {
			Action = event.getActionCommand();
			Type = (sjrb1.isSelected()) ? sjrb1.getText() : sjrb2.getText();
			ID = stextfield1.getText();
			Password = new String(this.spasswordfield1.getPassword());
			Retype_Passwd = new String(this.spasswordfield2.getPassword());
			Name = stextfield2.getText();
			Phone = stextfield3.getText();
			if(!(Password.equals(Retype_Passwd))){
				System.out.println(Password + " : " + Retype_Passwd);
				System.out.println("�н����� ����ġ");
			}
			else if(Type != null && ID != null && Password != null && Password.equals(Retype_Passwd) && Name != null && Phone != null){
				try {
					// ��½�Ʈ���� ȸ�������� ��|���� �����ڷ� ���
					dataOut.writeUTF(Action + "|" + Type + "|" + ID + "|" + Password + "|" + Name + "|" + Phone); 
					System.out.println((Action + "|" + Type + "|" + ID + "|" + Password + "|" + Name + "|" + Phone + "  daterout..."));
					// ��½�Ʈ���� ���۸� ��� �ٷ� ����
					dataOut.flush();
				} catch (Exception e) {
					// ���ܰ� �߻��ϸ� ������ ���¸� �ֿܼ� ǥ��
					System.out.println("Error : " + e.toString());
				}
			} 
			else{
				System.out.println("ȸ�������� ��� �Է��� �ּ���!");
			}
			
			try {
				success = dataIn.readUTF();
				System.out.println("ȸ������ : " + success);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(success.equals("success")){
				//TODO: ȸ������ �� �α��� ���ư���
			}
			else if (success.equals("fail")){
				//TODO: ����
			}
		}
		
		// �߻��� ActionEvent�� ActionCommnad(���⼭�� ��ư�� text)�� 		"Submit"�� ������ ����
		if (event.getActionCommand().equals("���")) {
			//TODO: �α��� â���� ��ȯ
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
		System.out.println("�α��� Ŭ���̾�Ʈ ���� �Ϸ�");
	}
	public static void main(String arts[]){
		inc = new InitClient();
	}
}
