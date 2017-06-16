package IWannaEat.Panel.guest;

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

import IWannaEat.info.Colors;
import IWannaEat.main.InitClient;

public class GuestPane extends JPanel implements ActionListener, Colors, Runnable{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
    protected Thread listener;
	
	private JPanel mainpn;
	private JPanel tpn; // table panel
	private JPanel tcard; // table card
	private JPanel mpn; // message panel
	private JPanel bpn; // button panel
	
	private JButton refresh;
	private JButton table[]; // table
	private JButton chat; // message? chat â
	private JButton select; //logout button
	private JButton exit;
	
	private String message = null;
	private String Name = null;
	String table_state = null;
	
	private int side = 0;
	private int colors[] = {0};
	private boolean tablechanged = false;
	
	public GuestPane(InitClient init, Socket socket, String name){
		//ó�� ���� ����
		Init=init;
		Name = name;
		try {
			this.socket = socket;
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
		//table�ɼ� ��û
		getOption();
		
		//������ ��ư ����, �׼Ǹ����� ���
		refresh = new JButton("���ΰ�ħ");
		refresh.addActionListener(this);
		chat = new JButton("ä��");
		chat.addActionListener(this);
		select = new JButton("����â");
		select.addActionListener(this);
		exit = new JButton("����");
		exit.addActionListener(this);
		table = new JButton[side*side];
		
		
		//tablePanel ����
		tpn = new JPanel();
		tcard = new JPanel();
		tpn.add(tcard);
		makeTable();
		// table �� ����
		setTable(table, side, colors);
		
		System.out.println("setted Table's Color");
		
		mpn = new JPanel();
		mpn.setLayout(new BorderLayout());
		mpn.add(chat,BorderLayout.CENTER);
		
		bpn = new JPanel();
		bpn.setLayout(new GridLayout(1,2,0,10));
		bpn.add(select);
		bpn.add(exit);
		
		mainpn = new JPanel();
		mainpn.setLayout(new GridLayout(4,1,5,10));
		mainpn.add(refresh);
		mainpn.add(tpn);
		mainpn.add(mpn);
		mainpn.add(bpn);
		
		add(mainpn);
		System.out.println("panel added...");
	}
	
	public synchronized void start() throws IOException {
        if (listener == null) {
            listener = new Thread(this);
            listener.start();
        }
    }
	public void stop(){
		if (listener != null) {
            listener.interrupt();
            listener = null;
            try {
				dataOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void run() {
		getOption();
		if(tablechanged){
			makeTable();
			setTable(table, side, colors);
		}
		try {
			wait(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("*")) {
			Init.getCardLayout().show(Init.getContentPane(), "SetOption");
		}
		else if (event.getActionCommand().equals("���ΰ�ħ")) {
			notify();
			getOption();
			makeTable();
			setTable(table, side, colors);
		}
		else if (event.getActionCommand().equals("����â")) {
			Init.getCardLayout().show(Init.getContentPane(), "Select");
			Init.getContentPane().remove(this);
		}
		else if (event.getActionCommand().equals("����")) {
			Init.stop();
			Init.dispose();
		}
	}
	
	public void makeTable(){
		tcard.removeAll();
		tcard.setLayout(new GridLayout(side,side,5,5));
		System.out.println("making table...");
		for(int i = 0; i < side*side; i++){
			table[i] = new JButton ();
			table[i].setEnabled(false); // ��ư Ŭ������ ���� �Ұ��� �ϵ��� ����
			table[i].setText(Integer.toString(i) + "�� ���̺�");
			table[i].addActionListener(this);
			tcard.add(table[i]);
		}
	}
	
	public void getOption(){
		try {
			dataOut.writeUTF("getOption" + "|" + Name);
			dataOut.flush();
			message = dataIn.readUTF();
			System.out.println(message);
			StringTokenizer stk = new StringTokenizer(message, "|");
			// ù ��ū�� ���̺��� �ٲ������ �ȹٲ������ same �Ǵ� differ�� �޴´�.
			table_state = stk.nextToken();
			if (table_state.equals("same") | table_state.equals("first"))
				tablechanged = false;
			else if (table_state.equals("differ"))
				tablechanged = true;
			// �ٲ������ �����Ѵ�.
			if (tablechanged){
				side = Integer.parseInt(stk.nextToken());
				colors = new int[side*side];
				Name = stk.nextToken();
				for(int i = 0; i< side*side; i++)
					colors[i] = Integer.parseInt(stk.nextToken());
			}
		} catch (IOException e) {
			System.out.println("option ��û/�ҷ����� ����");
			e.printStackTrace();
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
}
