package IWannaEat.Handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import IWannaEat.info.Member;
import IWannaEat.info.Option;

public class RestaurantHandler implements Runnable{
	protected Socket socket;
	String id = null;
	Option Op;
	/**
	 * ������ : InitHandler() 
	 * �����ڴ� Ŭ���̾�Ʈ�κ��� ���� ������ ���� 
	 * ������ socket�� �����ڸ� �޾� ������ ��.
	 */
	public RestaurantHandler(Socket socket, String rid) {
		this.socket = socket;
		this.id = rid;
		System.out.println(socket + "Restaurant �ڵ鷯 ���� ����");
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

	public void run() {
		
		try {
			String action = null;
			String message = null;
			String retaurant = null;
			while (!Thread.interrupted()) {
				message = dataIn.readUTF();
				System.out.println("get message : "+ message);
				StringTokenizer stk = new StringTokenizer(message, "|");
				action = stk.nextToken();
				if(action.equals("setoption")){
					int side = Integer.parseInt(stk.nextToken());
					String name = stk.nextToken();
					setOption(side, name);
				}
				else if(action.equals("getoption")){
					pushOption();
				}
				else if(action.equals("setTableOp")){
					int side = Integer.parseInt(stk.nextToken());
					String name = stk.nextToken();
					setTableOp(side, name, stk);
				}
				else if(action.equals("listUp")){
					listUp();
				}
				else if(action.equals("listDown")){
					listDown();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
				stop();
		}
	}
	
	//ó�� �α��� ���� ��, �ɼ��� �ٲ��� �� ���̺� ���� --> ������ �Ͼ������ ���̺��� ���� 
	public void setOption(int side, String name){
		Op = new Option();
		Op.setId(id);
		Op.setSide(side);
		Op.setName(name);
		for(int i = 0; i < side * side; i++)
			Op.setTable(Op.white, i);
		
		//�ɼ� ��ü�� ������ �ϰ� ������Ʈ �ƿ�ǲ���� ��������
		try{
			File dir = new File("./src/IWannaEat/info/option");
			String opPath = "./src/IWannaEat/info/option/" + Op.getId() + ".option";
			//���丮�� ������ ����
			if(dir.exists()==false){
				dir.mkdir();
			}
			ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(opPath));
			oos1.writeObject(Op);
			oos1.close();
			
		}catch (EOFException ee){
			System.out.println("��� ��Ʈ���� �ݾ��ּ���.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	//���̺� �ɼ�(����)�� ������ �� ���̺� ������ ������ �� ����
	//���̺��� ������ ���ڷ� �����Ͽ� (Color interface�� �ǹ� ����) ��ü�� ����
	public void setTableOp(int side, String name, StringTokenizer stk){
		int color = 0;
		
		Op = new Option();
		Op.setId(id);
		Op.setSide(side);
		Op.setName(name);
		for(int i = 0; i < side * side; i++){
			color = Integer.parseInt(stk.nextToken());
			Op.setTable(color, i);
		}
		
		try{
			File dir = new File("./src/IWannaEat/info/option");
			String opPath = "./src/IWannaEat/info/option/" + Op.getId() + ".option";
			//���丮�� ������ ����
			if(dir.exists()==false){
				dir.mkdir();
			}
			ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(opPath));
			oos1.writeObject(Op);
			oos1.close();
			
		}catch (EOFException ee){
			System.out.println("��� ��Ʈ���� �ݾ��ּ���.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	// ���̺��� ������ �� �ɼ��� �޾ƿ�, ������Ʈ ��������� ����Ǿ��ִ� option������ �ҷ��ͼ� "|"�� �����ڷ� �����͸� ����
	public void pushOption(){
		Op = new Option();
		String message = null;
		
		try{
			File dir = new File("./src/IWannaEat/info/option");
			String opPath = "./src/IWannaEat/info/option/" + id + ".option";
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(opPath));
			Op = (Option) ois.readObject();
			ois.close();
			message = Integer.toString(Op.getSide()) + "|" + Op.getName();
			for(int i = 0; i < Op.getSide()*Op.getSide(); i++)
				message += "|" + Op.getTable(i); 
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
	
	//�α��ν� �մ԰�ü���� ���Ը� ������ �� �ֵ��� list������ �����. 
	//listfile�� id�� ������ GuestHandler���� ���� id�� ����� option������ ã�� �� �ֵ��� ��
	public void listUp(){
		Op = new Option();
		String opPath = "./src/IWannaEat/info/option/" + id + ".option";
		String listDir = "./src/IWannaEat/info/uplist/";
		
		File dir = new File(listDir);
		if(dir.exists()==false){
			dir.mkdir();
		}
		
		String name = null;
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(opPath));
			Op = (Option)ois.readObject();
			name = Op.getName();
			File list = new File(listDir, name+".list");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(list));
			oos.writeObject(Op.getId());
			System.out.println("listup..");
			ois.close();
			oos.close();
		}catch (EOFException ee){
			System.out.println("���� ��Ʈ���� �ݾ��ּ���.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("listup - OptionŬ������ ã�� �� �����ϴ�.");
			e.printStackTrace();
		}
	}
	
	//�α׾ƿ� ��, �ɼ��� �����ϴ� ����� �ð����� list���� ������.
	public void listDown(){
		Op = new Option();
		String opPath = "./src/IWannaEat/info/option/" + id + ".option";
		String listDir = "./src/IWannaEat/info/uplist/";
		
		String name = null;
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(opPath));
			Op = (Option)ois.readObject();
			name = Op.getName();
			File list = new File(listDir, name+".list");
			list.delete();
			
			ois.close();
		}catch (EOFException ee){
			System.out.println("���� ��Ʈ���� �ݾ��ּ���.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("listup - OptionŬ������ ã�� �� �����ϴ�.");
			e.printStackTrace();
		}
	}
}
