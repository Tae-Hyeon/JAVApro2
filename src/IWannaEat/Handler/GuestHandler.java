package IWannaEat.Handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import IWannaEat.info.Member;
import IWannaEat.info.Option;

public class GuestHandler implements Runnable{
	protected Socket socket;
	
	/**
	 * ������ : InitHandler() 
	 * �����ڴ� Ŭ���̾�Ʈ�κ��� ���� ������ ���� 
	 * ������ socket�� �����ڸ� �޾� ������ ��.
	 */
	public GuestHandler(Socket socket, String gid) {
		this.socket = socket;
		System.out.println(socket + "Guest �ڵ鷯 ���� ����");
	}
	
	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	protected Thread listener;

	/**
	 * �޼ҵ� : init() 
	 * init()�� �Է�/��� ��Ʈ���� ����, ���۸� ������ ��Ʈ���� 
	 * ����Ͽ�, String�� �ְ���� �� �ִ� ����� ���� .
	 * Ŭ���̾�Ʈ ó���� �������� listener�� ���� ����� ����.
	 */
	public synchronized void init() {
		if (listener == null) {
			try {
			// �������κ��� �Է½�Ʈ���� ȹ��.
				dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				// �������κ��� ��½�Ʈ���� ȹ��.
				dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				// listener �����带 �����ϰ� ����.
				// this�� PizzaServer���� ������ handler ��ü�� �ǹ�.
				listener = new Thread(this);
				listener.start();
			} catch (IOException ignored) {
			}
		}

	}

	/**
	 * �޼ҵ� : stop() 
	 * listener �����忡 ���ͷ�Ʈ�� �ɰ� 
	 * dataOut ��Ʈ���� �ݴ� ����.
	 */
	public synchronized void stop() {
		if (listener != null) {
			try {
				if (listener != Thread.currentThread())
					listener.interrupt();
				listener = null;
				dataOut.close();
			}
			 catch (IOException ignored) {
			}
		}
	}

	// �޼ҵ� : run() 
	// �������� ������ ���� ���� ���۵Ǵ� �κ�
	//

	private Option PreOp = new Option();
	private Option NewOp = new Option();
	private boolean opchecked = false;
	private String resId = null;
	
	public void run() {
		
		try {
			
			String action = null;
			String message = null;
			String restaurant = null;
			while (!Thread.interrupted()) {
				message = dataIn.readUTF();
				System.out.println("message : "+message);
				StringTokenizer stk = new StringTokenizer(message, "|");
				action = stk.nextToken();
				if(action.equals("upload")){
					pushList();
				}
				else if(action.equals("select")){
					restaurant = stk.nextToken();
					saveSelectResId(restaurant);
				}
				else if(action.equals("getOption")){
					restaurant=stk.nextToken();
					pushOption(restaurant);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
				stop();
		}
	}
	
	//list ������ ComboBox���� Guest�� ������ �� �ֵ��� ����    
	public void pushList(){
		File listDir = new File("./src/IWannaEat/info/uplist");
		String fList[] = listDir.list();
		String message = new String();
		for(int i=0;i<fList.length;i++){
			message += fList[i].split("\\.", -1)[0];
			if (i != fList.length -1)
				message += "|";
		}
		System.out.println("�Ĵ� ����Ʈ : " + message);
		try {
			dataOut.writeUTF(message);
			dataOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pushOption(String retaurant){
		String message = null;
		
		try{
			File dir = new File("./src/IWannaEat/info/option");
			String opPath = "./src/IWannaEat/info/option/" + resId + ".option";
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(opPath));
			// ó������ PreOp�� ���� �� �� NewOp�� ������ ���� ��
			System.out.println(opPath +":"+ opchecked);
			if(opchecked)
				NewOp = (Option) ois.readObject();
			else
				PreOp = (Option) ois.readObject();
			
			ois.close();
			// �ɼ��� �ֱ������� üũ�� �� �ɼ��� �ٲ���� �ȹٲ���� üũ�Ѵ�.
			if(opchecked){
				if(PreOp.equals(NewOp))
					message = "same|";
				else{
					message = "differ|";
					message += Integer.toString(PreOp.getSide()) + "|" + PreOp.getName();
					for(int i = 0; i < PreOp.getSide()*PreOp.getSide(); i++)
						message += "|" + PreOp.getTable(i); 
					PreOp = NewOp;
				}
			}
			// ó���� ������ PreOp�� �ٷ��.
			else{
				message = "first|";
				message += Integer.toString(PreOp.getSide()) + "|" + PreOp.getName();
				for(int i = 0; i < PreOp.getSide()*PreOp.getSide(); i++)
					message += "|" + PreOp.getTable(i); 
				opchecked = true;
			}
			dataOut.writeUTF(message);
			dataOut.flush();
			System.out.println("table option pushed... : " + message);
		} catch (EOFException ee){
			System.out.println("��� ��Ʈ���� �ݾ��ּ���.");
			ee.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("getOption - OptionŬ������ ã�� �� �����ϴ�.");
			e.printStackTrace();
		}
	}
	
	public void saveSelectResId(String restaurant){
		File list = new File("./src/IWannaEat/info/uplist/"+restaurant+".list");
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(list));
			resId = (String) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("�Ĵ� id : " + resId);
	}
}
