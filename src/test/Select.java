package test;

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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import test.InitClient;

public class Select extends JPanel implements ActionListener{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private JPanel mpn;
	
	private JPanel cbp;
	private JPanel btp;
	
	private JLabel label;
	private JComboBox combo;
	
	private JButton jbt1;
	private JButton jbt2;
	
	public Select(InitClient init) {
		try {
			socket = new Socket("127.0.0.1", 8080);
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
		
		Init = init;
		
		label = new JLabel();
		label.setText("보고싶은 식당을 선택하세요!!");
		
		combo = new JComboBox();
		uploadList(combo);
		
		jbt1 = new JButton("선택");
		jbt1.addActionListener(this);
		jbt2 = new JButton("로그아웃");
		jbt2.addActionListener(this);
	
		cbp = new JPanel();
		cbp.setLayout(new GridLayout(2, 1, 0, 10));
		cbp.add(label);
		cbp.add(combo);
		
		btp = new JPanel();
		btp.setLayout(new GridLayout(1, 2, 10, 10));
		btp.add(jbt1);
		btp.add(jbt2);
		
		mpn = new JPanel();
		mpn.setLayout(new BorderLayout(20,20));
		mpn.add(cbp, BorderLayout.NORTH);
		mpn.add(btp, BorderLayout.SOUTH);
		
		add(mpn);
		
		setSize(500, 700);

		setVisible(true);
	}
	
	public synchronized void uploadList(JComboBox cb) {
		try {
			String message = dataIn.readUTF();
			StringTokenizer stk = new StringTokenizer(message, "|");
			int num = stk.countTokens();
			for (int i = 0; i < num; i++){
				cb.addItem(stk.nextToken());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
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
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
