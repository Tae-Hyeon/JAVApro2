package test;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JPanel;

import IWannaEat.Panel.guest.Select;
import IWannaEat.info.Colors;
import IWannaEat.main.InitClient;

public class RestaurantPane extends JPanel implements ActionListener, Colors{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private JPanel mainpn;
	private JPanel tpn; // table panel
	private JPanel tcard; // table card
	private JPanel mpn; // message panel
	private JPanel bpn; // button panel
	
	private JButton table[]; // table
	private JButton setToggle; // table 수정용 button
	private JButton chat; // message? chat 창
	private JButton set;
	private JButton logout; //logout button
	private JButton exit;
	
	private String message = null;
	private String name = null;
	private int side = 0;
	private int colors[];
	private boolean toggle = true;
	
	public RestaurantPane(InitClient init, Socket socket){
		//처음 시작 설정
		Init=init;
		try {
			this.socket = socket;
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
		//상호명 리스트에 등록 요청
		try{
			dataOut.writeUTF("listup");
			dataOut.flush();
		} catch (IOException e) {
			System.out.println("listup 요청실패");
			e.printStackTrace();
		}
		//table옵션 요청
		try {
			dataOut.writeUTF("getoption");
			dataOut.flush();
			message = dataIn.readUTF();
			StringTokenizer stk = new StringTokenizer(message, "|");
			side = Integer.parseInt(stk.nextToken());
			colors = new int[side*side];
			name = stk.nextToken();
			for(int i = 0; i< side*side; i++)
				colors[i] = Integer.parseInt(stk.nextToken());
		} catch (IOException e) {
			System.out.println("option 요청/불러오기 실패");
			e.printStackTrace();
		}
		
		//각각의 버튼 생성, 액션리스너 등록
		setToggle = new JButton("*");
		setToggle.addActionListener(this);
		chat = new JButton("채팅");
		chat.addActionListener(this);
		set = new JButton("테이블 변경");
		set.addActionListener(this);
		logout = new JButton("로그아웃");
		logout.addActionListener(this);
		exit = new JButton("종료");
		exit.addActionListener(this);
		table = new JButton[side*side];
		
		
		//tablePanel 구성
		tpn = new JPanel();
		tpn.setLayout(new GridLayout(side,side,5,5));
		System.out.println("making table...");
		for(int i = 0; i < side*side; i++){
			table[i] = new JButton ();
			table[i].setEnabled(false); // 처음에 버튼 클릭으로 설정 불가능 하도록 설정, 변경버튼을 통해 On/Off
			table[i].setText(Integer.toString(i) + "번 테이블");
			table[i].addActionListener(this);
			tpn.add(table[i]);
		}
		// table 색 설정
		setTable(table, side, colors);
		
		System.out.println("setted Table's Color");
		
		mpn = new JPanel();
		mpn.setLayout(new BorderLayout());
		mpn.add(chat,BorderLayout.CENTER);
		
		bpn = new JPanel();
		bpn.setLayout(new GridLayout(1,3,0,10));
		bpn.add(set);
		bpn.add(logout);
		bpn.add(exit);
		
		mainpn = new JPanel();
		mainpn.setLayout(new GridLayout(4,1,5,10));
		mainpn.add(setToggle);
		mainpn.add(tpn);
		mainpn.add(mpn);
		mainpn.add(bpn);
		
		add(mainpn);
		System.out.println("panel added...");
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
		if (event.getActionCommand().equals("*")) {
			Init.getCardLayout().show(Init.getContentPane(), "SetOption");
		}
		else if (event.getActionCommand().equals("테이블 변경")) {
			tableOnOff();
		}
		else if (event.getActionCommand().equals("변경 완료")) {
			pushOption();
			tableOnOff();
		}
		else if (event.getActionCommand().equals("로그아웃")) {
			Init.dispose();
			System.out.println("logout");
			InitClient inc = new InitClient();
		}
		else if (event.getActionCommand().equals("종료")) {
			Init.stop();
			Init.dispose();
		}
		
		for(int j=0; j<colors.length;j++){
			//count별로 버튼 색깔 표시 1이면 빨간색, 2이면 파란색, 3이면 검정색 표시
			if(event.getSource()==table[j]){
				changeColor(j);
				break;
			}
		}
	}
	
	public void setTable(JButton table[], int side, int color[]){
		for(int i = 0; i < side*side; i++){
			switch(color[i]){
			case white:
				table[i].setBackground(Color.white);
				break;
			case blue:
				table[i].setBackground(Color.blue);
				break;
			case red:
				table[i].setBackground(Color.red);
				break;
			case black:
				table[i].setBackground(Color.black);
				break;
			default :
				table[i].setBackground(Color.white);
			}
		}
	}
	
	public void pushOption(){
		message = "setTableOp" + side + "|" + name;
		for(int i = 0; i < colors.length; i++)
			message += "|" + Integer.toString(colors[i]); 
		try {
			dataOut.writeUTF(message);
			dataOut.flush();
		} catch (IOException e) {
			System.out.println("restaurant pane pushOption failed");
			e.printStackTrace();
		}
	}
	
	public void tableOnOff(){
		for(int i = 0; i < side*side; i++){
			table[i].setEnabled(toggle);
		}
		if(toggle == false)
			toggle = true;
		else
			toggle = false;
		set.setText("변경 완료");
	}
	
	public void changeColor(int index){
		if(colors[index]==3){
			colors[index]=0;
		}
		++colors[index];
		if(colors[index]==1){
			table[index].setBackground(Color.blue);
		}else if(colors[index]==2){
			table[index].setBackground(Color.red);
		}else if(colors[index]==3){
			table[index].setBackground(Color.black);
		}
	}
}

//TODO : 옵션바뀔때마다 setTable()실행
