package ps;
import java.io.*;
import java.net.*;
import java.util.*;

/*
 * class : PizzaHandler 
 * public class PizzaHandler�� ������ ���� Ŭ���̾�Ʈ���� ������ 
 * ���������� ó���ϴ� ���� ���� Thread Ŭ����.  
 * Ŭ���̾�Ʈ�κ����� �޽����� �޴� ����
 */
public class PizzaHandler implements Runnable {
	protected Socket socket;

	/**
	 * ������ : PizzaHandler() 
	 * �����ڴ� Ŭ���̾�Ʈ�κ��� ���� ������ ���� 
	 * ������ socket�� �����ڸ� �޾� ������ ��.
	 */
	public PizzaHandler(Socket socket) {
		this.socket = socket;
		System.out.println(socket + "�ڵ鷯 ��ü ����");
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
				dataIn = new DataInputStream(				new BufferedInputStream(socket.getInputStream()));
				// �������κ��� ��½�Ʈ���� ȹ��.
				dataOut = new DataOutputStream(				new BufferedOutputStream(socket.getOutputStream()));
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
	private Date pdate;

	public void run() {
		
		try {
			
			while (!Thread.interrupted()) {
				String message = dataIn.readUTF();
				try {
					
		// �� ���α׷������� ������ ������ ������ ��Ʈ���� �����Ͽ����� 
		// Ŭ���̾�Ʈ ���� �������� �ڷḦ �� �پ� �о� ���̴� �۾� ����.
		// ���� Ŭ���̾�Ʈ�� ���� ���α׷��� �ۼ������� �ʾ�����
                    // Ŭ���̾�Ʈ���� ���������� ���� ���� ������ ������ ������
                    // ���� ������ '|'�� �ΰ� �� �ٷ� ����.
		// ���� ������ ������ ���α׷������� �����ڸ� ã�Ƴ��� �� �ٿ� 
		// ��� ���ԵǾ� �ִ� ������ ���� �и��� ���� �۾��� ����. 
		// �̷��� �۾��� �Ľ��̶�� ��.	
					
		// ������ ��|���� ������ StringTokenizer�� ��ü�� �����մϴ�.
			StringTokenizer stk = new StringTokenizer(message, "|");
		// nextToken() �޼ҵ带 �̿��� �Ľ��� ��ū�� ������ name�� ����.
			String name = stk.nextToken();
		// ���� ��ū�� ������ �����մϴ�.
			String address = stk.nextToken();
			String phone = stk.nextToken();
			String pkind = stk.nextToken();
			String psize = stk.nextToken();
					
		// ������ Ŭ���̾�Ʈ�� ������ ������ ȭ�鿡 ���.
		// ȭ�鿡 ����� ������ ���ο� Date Ŭ������
		// �����Ͽ� Ŭ���̾�Ʈ�� ������ ���� �ð��� ���ÿ� ��� ��Ű�� ���� ����
			pdate = new Date();
			System.out.println(pdate.toString());
			System.out.println("�ֹ��� ���� : " + name);
			System.out.println("��� �ּ� : " + address);
			System.out.println("����ó : " + phone);
			System.out.println("�ֹ����� : " + pkind + " " + psize + "\n");
		} catch (NoSuchElementException e) {
					stop();
			}
		}
	} 
	catch (EOFException ignored) {
			System.out.println( "������ �����ϼ̽��ϴ�.");
	}
	catch (IOException ie) {
			if (listener == Thread.currentThread())
				ie.printStackTrace();
	} finally {
			stop();
		}
	}
}