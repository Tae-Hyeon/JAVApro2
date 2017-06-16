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
	
	//list 정보를 ComboBox에서 Guest가 선택할 수 있도록 보냄    
	public void pushList(){
		File listDir = new File("./src/IWannaEat/info/uplist");
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
	
	public void pushOption(String retaurant){
		String message = null;
		
		try{
			File dir = new File("./src/IWannaEat/info/option");
			String opPath = "./src/IWannaEat/info/option/" + resId + ".option";
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(opPath));
			// 처음에만 PreOp에 저장 그 후 NewOp에 저장해 전과 비교
			System.out.println(opPath +":"+ opchecked);
			if(opchecked)
				NewOp = (Option) ois.readObject();
			else
				PreOp = (Option) ois.readObject();
			
			ois.close();
			// 옵션을 주기적으로 체크할 때 옵션이 바뀌었나 안바뀌었나 체크한다.
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
			// 처음은 무조건 PreOp만 다룬다.
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
			System.out.println("출력 스트림을 닫아주세요.");
			ee.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("getOption - Option클래스를 찾을 수 없습니다.");
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
		System.out.println("식당 id : " + resId);
	}
}
