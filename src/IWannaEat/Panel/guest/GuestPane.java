package IWannaEat.Panel.guest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JPanel;

import IWannaEat.main.InitClient;

public class GuestPane extends JPanel implements ActionListener{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private JPanel mainpn; // panel들을 담을 main panel
	private JPanel tpn; // table panel
	private JPanel mpn; // message panel
	private JPanel bpn; // button panel
	
	private JButton table[]; // table button
	private JButton setToggle; // set table button
	private JButton exit; // exit button
	
	public GuestPane(InitClient init, Socket socket){
		
	}
	
	public synchronized void setTable(JButton table, int side){
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
