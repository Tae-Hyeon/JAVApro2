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

	// Panel�� ���� mainpanel
	private JPanel lmp;
	
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
	
	public LoginPane(InitClient init, Socket socket){
		Init = init;
		System.out.println("�α��� Ŭ���̾�Ʈ ������ ����...");
		// main Panel ����, ��� - label, �ߴ� - ID, Password�Է�, �ϴ� - ��ư layout ����
		lmp = new JPanel();
		lmp.setLayout(new BorderLayout(20,20));
		
		// JTextField�� ��Ÿ���� �� ����
		ltextfield = new JTextField("ID");
		lpasswordfield = new JPasswordField("Password");
		
		lbutton1 = new JButton("�α���");
		lbutton1.addActionListener(this);
		lbutton2 = new JButton("ȸ������");
		lbutton2.addActionListener(this);
		lbutton3 = new JButton("����");
		lbutton3.addActionListener(this);
		
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
		lpanel3.setLayout(new GridLayout(1,3,20,0));
		lpanel3.add(lbutton1);
		lpanel3.add(lbutton2);
		lpanel3.add(lbutton3);
		
		// main Panel�� panel�߰�
		lmp.add(lpanel1,BorderLayout.NORTH);
		lmp.add(lpanel2,BorderLayout.CENTER);
		lmp.add(lpanel3,BorderLayout.SOUTH);
		
		add(lmp);
		// GUI������
		setSize(500, 700);

		setVisible(true);
		
		try {
			// ip, port�� ���ڷ� Socket�� ��ü�� ����
			// ����� ȯ�濡 ���� ip�� port���� �����ؼ� ���
			this.socket = socket;
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
		String isSet = null;
		
		String Action = null;
		String ID = null;
		String Password = null;
		
		//ID, Password�� ������ �����ϴ� ��� --> �Լ��� �߻�ȭ?? �ؾ���
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
				success = stk.nextToken();
					
				System.out.println("�α��� : " + success);
				if(success.equals("success")){
					Type = stk.nextToken(); 
					ID = stk.nextToken();
					if (Type.equals("Guest")){
						//TODO: �α��� �Ϸ� -- �մ� ��ü ���� cardLayout�� �߰�
						System.out.println("guest panel ����...");
						Init.getContentPane().add("Select", new Select(Init, socket));
						Init.getCardLayout().show(Init.getContentPane(), "Select");
					}
					else if (Type.equals("Restaurant")){
						//TODO: �α��� �Ϸ� -- ���� ��ü ����  xx option�� �ִ��� �������� ��Ʈ������ �޾Ƽ� �װͿ� ���� ���� ó��
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
					//TODO: ����
					llabel2.setText("��й�ȣ�� Ȯ���� �ּ���.");
				}
				else if (success.equals("none")){
					//id�� ���� ���
					llabel2.setText("ȸ�������� ���ּ���!!");
				}
			}catch (IOException e){
				System.out.println("Error : " + e.toString());
			}
		}
		
		// �߻��� ActionEvent�� ActionCommnad(���⼭�� ��ư�� text)�� 		"ȸ������"�� ������ ����
		else if (event.getActionCommand().equals("ȸ������")) {
			//TODO: ȸ������ ��ȯ
			System.out.println("ȸ������ ����");
			Init.getCardLayout().show(Init.getContentPane(), "Signin");
		}
		else if (event.getActionCommand().equals("����")) {
			//TODO: ȸ������ ��ȯ
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
