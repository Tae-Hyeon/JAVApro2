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
				pushList();
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
			while (!Thread.interrupted()) {
				
			}
		}finally {
				stop();
		}
	}
	
	public void pushList(){
		File listDir = new File("./src/test/uplist");
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
}
