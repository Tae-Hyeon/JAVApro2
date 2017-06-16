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
	 * 생성자 : InitHandler() 
	 * 생성자는 클라이언트로부터 들어온 연결을 맡은 
	 * 소켓인 socket의 참조자를 받아 복사해 둠.
	 */
	public RestaurantHandler(Socket socket, String rid) {
		this.socket = socket;
		this.id = rid;
		System.out.println(socket + "Restaurant 핸들러 소켓 생성");
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
	
	//처음 로그인 했을 때, 옵션을 바꿨을 때 테이블 구성 --> 무조건 하얀색으로 테이블을 구성 
	public void setOption(int side, String name){
		Op = new Option();
		Op.setId(id);
		Op.setSide(side);
		Op.setName(name);
		for(int i = 0; i < side * side; i++)
			Op.setTable(Op.white, i);
		
		//옵션 객체에 저장을 하고 오브젝트 아웃풋으로 파일저장
		try{
			File dir = new File("./src/IWannaEat/info/option");
			String opPath = "./src/IWannaEat/info/option/" + Op.getId() + ".option";
			//디렉토리가 없으면 생성
			if(dir.exists()==false){
				dir.mkdir();
			}
			ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(opPath));
			oos1.writeObject(Op);
			oos1.close();
			
		}catch (EOFException ee){
			System.out.println("출력 스트림을 닫아주세요.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	//테이블 옵션(색깔)을 변경한 후 테이블 변경을 눌렀을 때 실행
	//테이블의 색깔을 숫자로 저장하여 (Color interface로 의미 정의) 객체에 저장
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
			//디렉토리가 없으면 생성
			if(dir.exists()==false){
				dir.mkdir();
			}
			ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(opPath));
			oos1.writeObject(Op);
			oos1.close();
			
		}catch (EOFException ee){
			System.out.println("출력 스트림을 닫아주세요.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	// 테이블을 구성할 시 옵션을 받아옴, 오브젝트 입출력으로 저장되어있는 option파일을 불러와서 "|"을 구분자로 데이터를 보냄
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
			System.out.println("출력 스트림을 닫아주세요.");
			ee.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("getOption - Option클래스를 찾을 수 없습니다.");
			e.printStackTrace();
		}
	}
	
	//로그인시 손님객체에서 가게를 선택할 수 있도록 list파일을 만든다. 
	//listfile에 id를 저장해 GuestHandler에서 가게 id로 저장된 option파일을 찾을 수 있도록 함
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
			System.out.println("딥력 스트림을 닫아주세요.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("listup - Option클래스를 찾을 수 없습니다.");
			e.printStackTrace();
		}
	}
	
	//로그아웃 시, 옵션을 변경하는 잠깐의 시간동안 list에서 내린다.
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
			System.out.println("딥력 스트림을 닫아주세요.");
			ee.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("listup - Option클래스를 찾을 수 없습니다.");
			e.printStackTrace();
		}
	}
}
