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
	
	// Panel �� ���� mainpanel
	private JPanel smp;
	
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
		
	public SigninPane(InitClient init){
		Init = init;
	// main Panel ����, ��� - label, �ߴ� - RadioButton, ID, Password�Է�, �ϴ� - ��ư layout ����
		smp = new JPanel();
		smp.setLayout(new BorderLayout(20,20));	
		
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
		sbutton2.addActionListener(this);
		
		
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
		
	// main Panel�� panel�߰�
		smp.add(spanel1,BorderLayout.NORTH);
		smp.add(spanel3,BorderLayout.CENTER);
		smp.add(spanel4,BorderLayout.SOUTH);
		
		add(smp);
		// GUI������
		
		setSize(500, 700);
	
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
		String success = "fail";
		
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
					success = "success";
				} catch (Exception e) {
					// ���ܰ� �߻��ϸ� ������ ���¸� �ֿܼ� ǥ��
					System.out.println("Error : " + e.toString());
				}
			} 
			else{
				System.out.println("ȸ�������� ��� �Է��� �ּ���!");
			}
			
			try {
				System.out.println("data in");
				if (success == "success")
					success = dataIn.readUTF();
				System.out.println("ȸ������ : " + success);
			} catch (IOException e) {  
				e.printStackTrace();
			}
			if(success.equals("success")){
				System.out.println("�α��� :SUCCESS");
				Init.getCardLayout().show(Init.getContentPane(), "Login");
			}
			else if (success.equals("fail")){
				//TODO: ����
			}
		}
		
		// �߻��� ActionEvent�� ActionCommnad(���⼭�� ��ư�� text)�� 		"Submit"�� ������ ����
		if (event.getActionCommand().equals("���")) {
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
