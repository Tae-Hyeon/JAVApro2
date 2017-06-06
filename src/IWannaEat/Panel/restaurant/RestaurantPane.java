package IWannaEat.Panel.restaurant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JPanel;

import IWannaEat.main.InitClient;

public class RestaurantPane extends JPanel implements ActionListener{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private JPanel mainpn;
	private JPanel tpn; // table panel
	private JPanel mpn; // message panel
	private JPanel bpn; // button panel
	
	private JButton table[]; // table
	private JButton setToggle; // table 수정용 button
	private JButton message; // message 창
	private JButton set;
	private JButton logout; //logout button
	private JButton exit;
	
	public RestaurantPane(InitClient init, Socket socket){
		try {
			this.socket = socket;
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
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
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}
}
