package ps;
import java.io.*;
import java.net.*;
import java.util.*;

/*
 * class : PizzaHandler 
 * public class PizzaHandler는 서버로 들어온 클라이언트와의 연결을 
 * 개별적으로 처리하는 일을 맡은 Thread 클래스.  
 * 클라이언트로부터의 메시지를 받는 역할
 */
public class PizzaHandler implements Runnable {
	protected Socket socket;

	/**
	 * 생성자 : PizzaHandler() 
	 * 생성자는 클라이언트로부터 들어온 연결을 맡은 
	 * 소켓인 socket의 참조자를 받아 복사해 둠.
	 */
	public PizzaHandler(Socket socket) {
		this.socket = socket;
		System.out.println(socket + "핸들러 객체 생성");
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
				dataIn = new DataInputStream(				new BufferedInputStream(socket.getInputStream()));
				// 소켓으로부터 출력스트림을 획득.
				dataOut = new DataOutputStream(				new BufferedOutputStream(socket.getOutputStream()));
				// listener 스레드를 생성하고 시작.
				// this는 PizzaServer에서 생성된 handler 객체를 의미.
				listener = new Thread(this);
				listener.start();
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
	private Date pdate;

	public void run() {
		
		try {
			
			while (!Thread.interrupted()) {
				String message = dataIn.readUTF();
				try {
					
		// 이 프로그램에서는 생성된 소켓을 가지고 스트림을 구성하였으며 
		// 클라이언트 에서 보내어진 자료를 한 줄씩 읽어 들이는 작업 수행.
		// 아직 클라이언트에 대한 프로그램을 작성하지는 않았지만
                    // 클라이언트에서 보내어지는 여러 가지 정보는 각각의 정보에
                    // 대해 구분자 '|'를 두고 한 줄로 보냄.
		// 따라서 서버의 스레드 프로그램에서는 구분자를 찾아내어 한 줄에 
		// 모두 포함되어 있는 정보를 각각 분리해 내는 작업을 수행. 
		// 이러한 작업을 파싱이라고 함.	
					
		// 구분자 “|”를 가지는 StringTokenizer형 객체를 생성합니다.
			StringTokenizer stk = new StringTokenizer(message, "|");
		// nextToken() 메소드를 이용해 파싱한 토큰을 가져와 name에 설정.
			String name = stk.nextToken();
		// 다음 토큰을 가져와 설정합니다.
			String address = stk.nextToken();
			String phone = stk.nextToken();
			String pkind = stk.nextToken();
			String psize = stk.nextToken();
					
		// 서버에 클라이언트가 보내온 정보를 화면에 출력.
		// 화면에 출력할 때마다 새로운 Date 클래스를
		// 생성하여 클라이언트가 정보를 보낸 시간을 동시에 출력 시키는 역할 수행
			pdate = new Date();
			System.out.println(pdate.toString());
			System.out.println("주문자 성명 : " + name);
			System.out.println("배달 주소 : " + address);
			System.out.println("연락처 : " + phone);
			System.out.println("주문피자 : " + pkind + " " + psize + "\n");
		} catch (NoSuchElementException e) {
					stop();
			}
		}
	} 
	catch (EOFException ignored) {
			System.out.println( "접속을 종료하셨습니다.");
	}
	catch (IOException ie) {
			if (listener == Thread.currentThread())
				ie.printStackTrace();
	} finally {
			stop();
		}
	}
}