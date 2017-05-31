package IWannaEat;

import java.io.*;
import java.net.*;
import java.util.*;
class Member implements Serializable{
	private String id;
	private String password;
	private String name;
	private String phone;
	private String type;
	private Date pdate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {  
		this.type = type;
	}
	public Date getPdate() {
		return pdate;
	}
	public void setPdate(Date pdate) {
		this.pdate = pdate;
	}
}

/*
 * class : InitHandler 
 * public class InitHandler는 서버로 들어온 클라이언트와의 연결을 
 * 개별적으로 처리하는 일을 맡은 Thread 클래스.  
 * 클라이언트로부터의 메시지를 받는 역할
 */
public class InitHandler implements Runnable {
	protected Socket socket;

	/**
	 * 생성자 : InitHandler() 
	 * 생성자는 클라이언트로부터 들어온 연결을 맡은 
	 * 소켓인 socket의 참조자를 받아 복사해 둠.
	 */
	public InitHandler(Socket socket) {
		this.socket = socket;
		System.out.println(socket + "핸들러 소켓 생성");
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
				String message = dataIn.readUTF();
				String outmessage = null;
				String success = null; // 성공 여부
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
					String action = new String(stk.nextToken());
					
				// 회원가입 실행 시 :
					if(action.equals("회원가입")){
						Member member = new Member();
					// nextToken() 메소드를 이용해 파싱한 토큰을 가져와 member 객체에 설정.
						member.setType(stk.nextToken());
						member.setId(stk.nextToken());
						member.setPassword(stk.nextToken());
						member.setName(stk.nextToken());
						member.setPhone(stk.nextToken());
						member.setPdate(new Date());
						System.out.println("member 객체에 저장");
					// TODO: 핸들러 변경 타이밍 확인, 넘겨줄 정보 더 없나 확인, 핸들러에서 받을 정보 확인 ,, 로그인도 마찬가지
					// TODO: dataout이 제대로 되지 않는다 수정, 이미 id.txt파일이 있을경우 처리문 생성
					// 서버에 클라이언트가 보내온 정보를 해당 id.txt파일에 객체로 저장
						try{
							System.out.println(member.getId() + ".txt 생성");
							ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(member.getId()+".txt"));
							success = "success";
							outmessage = member.getType() + "|" + success;
							oos1.writeObject(member);
							change_handler(member.getType());
							oos1.close();
						}catch (EOFException ee){
							System.out.println("출력 스트림을 닫아주세요.");
							ee.printStackTrace();
						}catch (IOException ie) {
							message = success = "fail";
							ie.printStackTrace();
						}
					}
					else if (action.equals("로그인")){
						String id = stk.nextToken();
						String password = stk.nextToken();
						try{
							ObjectInputStream ois = new ObjectInputStream(new FileInputStream(id + ".txt"));
							Member member = (Member)ois.readObject();
							if(member.getId().equals(id) && member.getPassword().equals(password)){
								success = "success";
								outmessage = member.getType() + "|" + success; 
							}
							else
								outmessage = success = "fail";
							dataOut.writeUTF(outmessage);
							change_handler(member.getType());
							ois.close();
						} catch(FileNotFoundException e){
							System.out.println(e + "파일을 찾을 수 없습니다.");
							e.printStackTrace();
						} catch (IOException ie) {
							ie.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				} catch (NoSuchElementException e) {
							stop();
				}
			}
		} catch (EOFException ignored) {
				System.out.println( "접속을 종료하셨습니다.");
		} catch (IOException ie) {
				if (listener == Thread.currentThread())
					ie.printStackTrace();
		} finally {
				stop();
		}
	}
	
	public void change_handler(String type){
		/*
		ServerSocket server = new ServerSocket(8080);
		System.out.println("Server Started..!!!");
		if(type.equals("restaurant")){
			// 다수의 클라이언트의 접속을 받아드리기 위해서 무한반복 수행.
			while (true) {
				// 클라이언트의 접속을 대기.
				// 클라이언트가 접속하면 Socket의 객체 client를 생성.
			Socket client = server.accept();
			// 접속한 클라이언트의 정보를 출력.
			System.out.println("Accepted from: " + client.getInetAddress());
			// 클라이언트 socket객체를 인자로 ClientHandler형 객체를 생성.
			RestaurantHandler rhandler = new RestaurantHandler(client);
			// Restauranthandler의 init() 메소드를 호출.
			rhandler.init();
			}
		}
		else if(type.equals("client")){
			// 다수의 클라이언트의 접속을 받아드리기 위해서 무한반복 수행.
			while (true) {
				// 클라이언트의 접속을 대기.
				// 클라이언트가 접속하면 Socket의 객체 client를 생성.
			Socket client = server.accept();
			// 접속한 클라이언트의 정보를 출력.
			System.out.println("Accepted from: " + client.getInetAddress());
			// 클라이언트 socket객체를 인자로 ClientHandler형 객체를 생성.
			ClientHandler chandler = new ClientHandler(client);
			// Clienthandler의 init() 메소드를 호출.
			chandler.init();
			}
		}*/
	}
}

