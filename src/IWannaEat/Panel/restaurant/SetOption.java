package IWannaEat.Panel.restaurant;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import IWannaEat.Panel.guest.GuestPane;
import IWannaEat.main.InitClient;

public class SetOption extends JPanel implements ActionListener{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private JPanel mpn;
	
	private JPanel slp;
	private JPanel btp;
	
	private JLabel label1;
	private JLabel label2;
	
	private JComboBox combo;
	
	private TextField name;
	
	private JButton jbt1;
	private JButton jbt2;
	
	public SetOption(InitClient init, Socket socket){
		try {
			this.socket = socket;
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
		
		//cardlayout�� �ֱ� ���� InitClient�� �޴´�
		Init = init;
		
		label1 = new JLabel();
		label1.setText("Ź�� ����");
		label2 = new JLabel();
		label2.setText("��ȣ�� ����");
		
		//���� ����Ʈ�� ���� ComboBox
		combo = new JComboBox();
		combo.addItem("3x3");
		combo.addItem("4x4");
		combo.addItem("5x5");
		
		//���� �� ������ ���� TextField
		name = new TextField();
		
		//���Ը���Ʈ reupload, �ش簡�� ����, �α׾ƿ��� ���� ��ư ����
		jbt1 = new JButton("����");
		jbt1.addActionListener(this);
		jbt2 = new JButton("���");
		jbt2.addActionListener(this);
	
		// �޺��ڽ��� �� �г�
		slp = new JPanel();
		slp.setLayout(new GridLayout(2, 2, 10, 10));
		slp.add(label1);
		slp.add(combo);
		slp.add(label2);
		slp.add(name);
		
		// ����, �α׾ƿ� ��ư�� �� �г�
		btp = new JPanel();
		btp.setLayout(new GridLayout(1, 2, 10, 10));
		btp.add(jbt1);
		btp.add(jbt2);
		
		//���� �гο� �� �г� �߰�
		mpn = new JPanel();
		mpn.setLayout(new BorderLayout(20,20));
		mpn.add(slp, BorderLayout.NORTH);
		mpn.add(btp, BorderLayout.SOUTH);
		
		add(mpn);
		
		setSize(500, 700);

		setVisible(true);
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
	@Override
	public void actionPerformed(ActionEvent event) {
		String message = null;
		if(event.getActionCommand().equals("����"))
        {
			message = "setoption" + "|" +combo.getSelectedItem().toString().split("x",-1)[0] + "|" + name.getText();
			try {
				dataOut.writeUTF(message);
				dataOut.flush();
			} catch (IOException e) {
				System.out.println(message + "setoption fail");
				e.printStackTrace();
			}
			Init.getCardLayout().show(Init.getContentPane(), "Restaurant");
        }
		else if(event.getActionCommand().equals("���"))
        {
			Init.getCardLayout().show(Init.getContentPane(), "Restaurant");
        }
	}

}
