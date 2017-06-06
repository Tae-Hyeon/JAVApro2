package IWannaEat.Panel.guest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JPanel;

import IWannaEat.main.InitClient;

public class GuestPane {
	private InitClient init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private JPanel mainpn;
	private JPanel tpn;
	private JPanel mpn;
	private JPanel bpn;
	
	private JButton table[];
	private JButton setToggle;
	private JButton jbt1;
	private JButton jbt2;
}
