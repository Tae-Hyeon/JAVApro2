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
	 * 생성자 : InitHandler() 
	 * 생성자는 클라이언트로부터 들어온 연결을 맡은 
	 * 소켓인 socket의 참조자를 받아 복사해 둠.
	 */
	public GuestHandler(Socket socket, String gid) {
		this.socket = socket;
		System.out.println(socket + "Guest 핸들러 소켓 생성");
	}
	
	protected DataInputStream dataIn;
	protected DataOutputStream dataOut;
	protected Thread listener;

	/**
	 * 메소드 : init() 
	 * init()는 입력/출력 스트림을 열고, 버퍼링 데이터 스트림을 
	 * 사용하여, String을 주고받을 수 있는 기능을 제공 .
	 * 클라이언트 처리용 쓰레드인 listener를 새로 만들어 시작.
	 */
	public synchronized void init() {
		if (listener == null) {
			try {
			// 소켓으로부터 입력스트림을 획득.
				dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				// 소켓으로부터 출력스트림을 획득.
				dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				// listener 스레드를 생성하고 시작.
				// this는 PizzaServer에서 생성된 handler 객체를 의미.
				listener = new Thread(this);
				listener.start();
				pushList();
			} catch (IOException ignored) {
			}
		}

	}

	/**
	 * 메소드 : stop() 
	 * listener 쓰레드에 인터럽트를 걸고 
	 * dataOut 스트림을 닫는 역할.
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

	// 메소드 : run() 
	// 쓰레드의 실행이 가장 먼저 시작되는 부분
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
		System.out.println("식당 리스트 : " + message);
		try {
			dataOut.writeUTF(message);
			dataOut.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
