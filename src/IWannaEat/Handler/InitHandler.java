package IWannaEat.Handler;

import java.io.*;
import java.net.*;
import java.util.*;

import IWannaEat.info.Member;

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
				// 회원 정보 관련 String	
					String success = "fail"; // 성공 여부
					String type = null;	//손님, 가게
					String path = null; // 파일 경로
					String fList []; // 파일 리스트
					boolean check = false;
					
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
					// 서버에 클라이언트가 보내온 정보를 해당 id.txt파일에 객체로 저장
						try{
							// id.txt파일이 있는지 판단 과정
							File dir = new File("./src/IWannaEat/info/id");
							//디렉토리가 없으면 생성
							if(dir.exists()==false){
								dir.mkdir();
							}
							//디렉토리 안에 있는 리스트 가져오기
							fList =dir.list();
							
							String pt =member.getId()+".txt";
							for(int i=0;i<fList.length;i++){
								if(pt.equals(fList[i])){
									//등록된 사용자면 true반환 미등록 사용자면 false
									check=true;
								}
							}
							
							// check == false 즉, 회원가입 안된 id를 가입시켜줌
							if (!check){
								path = "./src/IWannaEat/info/id/" + member.getId() + ".txt";
								System.out.println(member.getId() + ".txt 생성");
								ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(path));
								outmessage = success = "success";
								oos1.writeObject(member);
								oos1.close();
							}
							else{
								//TODO: 이미 가입한 ID입니다.
							}
						}catch (EOFException ee){
							System.out.println("출력 스트림을 닫아주세요.");
							ee.printStackTrace();
						}catch (IOException ie) {
							ie.printStackTrace();
						}
						System.out.println("data out");
						dataOut.writeUTF(outmessage);
						// 출력스트림의 버퍼를 비워 바로 전송
						dataOut.flush();
					}
					else if (action.equals("로그인")){
						String id = stk.nextToken();
						String password = stk.nextToken();
						File rdir = new File("./src/IWannaEat/info/uplist"); //로그인한 가게 리스트 저장 디렉토리
						
						if(rdir.exists()==false){
							rdir.mkdir();
						}
						path = "./src/IWannaEat/info/id/" + id + ".txt";
						
						
						try{
							ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
							Member member = (Member)ois.readObject();
							//TODO : 타입이 restaurant 인지 client인지에 따라 보내주는 정보가 다르게 처리
							if(member.getId().equals(id) && member.getPassword().equals(password)){
								type = member.getType();
								success = "success";
								outmessage = success + "|" + type +"|" + member.getName();
								System.out.println(success + " : " + outmessage);
								// 가게일 경우 리스트에 등록 --> 회원이 볼 수 있는 리스트
								// TODO : name으로 저장할 경우 불러오고 선택 시 판단하지를 못함
								if(type == "Restaurant"){
									File rtListFile = new File("./src/IWannaEat/info/uplist/" + member.getName() + ".list");
								}
							}
							else
								outmessage = success;
							
							dataOut.writeUTF(outmessage);
							dataOut.flush();
							
							if(success == "success"){
								change_handler(socket, member.getType(), member.getId());
							}
							ois.close();
						} catch(FileNotFoundException e){
							System.out.println(e + "파일을 찾을 수 없습니다.");
							//TODO : 회원가입을 해주세용
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
	
	public void change_handler(Socket socket, String type, String id){
		// 타입에 따라 소켓을 매개변수로 해당 핸들러 객체 생성
		if(type.equals("Restaurant")){
			//RestaurantHandler rhandler = new RestaurantHandler(socket, id);
			// Restauranthandler의 init() 메소드를 호출.
			//rhandler.init();
		}
		else if(type.equals("Guest")){
			GuestHandler ghandler = new GuestHandler(socket, id);
			// Guesthandler의 init() 메소드를 호출.
			ghandler.init();
		}
	}
}

