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

import IWannaEat.main.InitClient;

public class FirstSet extends JPanel implements ActionListener{
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
	
	public FirstSet(InitClient init, Socket socket){
		try {
			this.socket = socket;
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
		
		//cardlayout에 넣기 위해 InitClient를 받는다
		Init = init;
		
		label1 = new JLabel();
		label1.setText("탁자 갯수");
		label2 = new JLabel();
		label2.setText("상호명 설정");
		
		//가게 리스트를 위한 ComboBox
		combo = new JComboBox();
		combo.addItem("3x3");
		combo.addItem("4x4");
		combo.addItem("5x5");
		
		//가게 명 설정을 위한 TextField
		name = new TextField();
		
		//가게리스트 reupload, 해당가게 선택, 로그아웃을 위한 버튼 생성
		jbt1 = new JButton("변경");
		jbt1.addActionListener(this);
	
		// 콤보박스가 들어갈 패널
		slp = new JPanel();
		slp.setLayout(new GridLayout(2, 2, 10, 10));
		slp.add(label1);
		slp.add(combo);
		slp.add(label2);
		slp.add(name);
		
		// 선택, 로그아웃 버튼이 들어갈 패널
		btp = new JPanel();
		btp.add(jbt1);
		
		//메인 패널에 각 패널 추가
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
		if(event.getActionCommand().equals("변경"))
        {
            //TODO : dataOut을 통한 option파일 생성
			Init.getContentPane().add("Restaurant", new RestaurantPane(Init, socket));
			Init.getCardLayout().show(Init.getContentPane(), "Restaurant");
        }
	}

}

