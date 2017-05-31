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
 * public class InitHandler�� ������ ���� Ŭ���̾�Ʈ���� ������ 
 * ���������� ó���ϴ� ���� ���� Thread Ŭ����.  
 * Ŭ���̾�Ʈ�κ����� �޽����� �޴� ����
 */
public class InitHandler implements Runnable {
	protected Socket socket;

	/**
	 * ������ : InitHandler() 
	 * �����ڴ� Ŭ���̾�Ʈ�κ��� ���� ������ ���� 
	 * ������ socket�� �����ڸ� �޾� ������ ��.
	 */
	public InitHandler(Socket socket) {
		this.socket = socket;
		System.out.println(socket + "�ڵ鷯 ���� ����");
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
			
			while (!Thread.interrupted()) {
				String message = dataIn.readUTF();
				String outmessage = null;
				String success = null; // ���� ����
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
					String action = new String(stk.nextToken());
					
				// ȸ������ ���� �� :
					if(action.equals("ȸ������")){
						Member member = new Member();
					// nextToken() �޼ҵ带 �̿��� �Ľ��� ��ū�� ������ member ��ü�� ����.
						member.setType(stk.nextToken());
						member.setId(stk.nextToken());
						member.setPassword(stk.nextToken());
						member.setName(stk.nextToken());
						member.setPhone(stk.nextToken());
						member.setPdate(new Date());
						System.out.println("member ��ü�� ����");
					// TODO: �ڵ鷯 ���� Ÿ�̹� Ȯ��, �Ѱ��� ���� �� ���� Ȯ��, �ڵ鷯���� ���� ���� Ȯ�� ,, �α��ε� ��������
					// TODO: dataout�� ����� ���� �ʴ´� ����, �̹� id.txt������ ������� ó���� ����
					// ������ Ŭ���̾�Ʈ�� ������ ������ �ش� id.txt���Ͽ� ��ü�� ����
						try{
							System.out.println(member.getId() + ".txt ����");
							ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(member.getId()+".txt"));
							success = "success";
							outmessage = member.getType() + "|" + success;
							oos1.writeObject(member);
							change_handler(member.getType());
							oos1.close();
						}catch (EOFException ee){
							System.out.println("��� ��Ʈ���� �ݾ��ּ���.");
							ee.printStackTrace();
						}catch (IOException ie) {
							message = success = "fail";
							ie.printStackTrace();
						}
					}
					else if (action.equals("�α���")){
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
							System.out.println(e + "������ ã�� �� �����ϴ�.");
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
				System.out.println( "������ �����ϼ̽��ϴ�.");
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
			// �ټ��� Ŭ���̾�Ʈ�� ������ �޾Ƶ帮�� ���ؼ� ���ѹݺ� ����.
			while (true) {
				// Ŭ���̾�Ʈ�� ������ ���.
				// Ŭ���̾�Ʈ�� �����ϸ� Socket�� ��ü client�� ����.
			Socket client = server.accept();
			// ������ Ŭ���̾�Ʈ�� ������ ���.
			System.out.println("Accepted from: " + client.getInetAddress());
			// Ŭ���̾�Ʈ socket��ü�� ���ڷ� ClientHandler�� ��ü�� ����.
			RestaurantHandler rhandler = new RestaurantHandler(client);
			// Restauranthandler�� init() �޼ҵ带 ȣ��.
			rhandler.init();
			}
		}
		else if(type.equals("client")){
			// �ټ��� Ŭ���̾�Ʈ�� ������ �޾Ƶ帮�� ���ؼ� ���ѹݺ� ����.
			while (true) {
				// Ŭ���̾�Ʈ�� ������ ���.
				// Ŭ���̾�Ʈ�� �����ϸ� Socket�� ��ü client�� ����.
			Socket client = server.accept();
			// ������ Ŭ���̾�Ʈ�� ������ ���.
			System.out.println("Accepted from: " + client.getInetAddress());
			// Ŭ���̾�Ʈ socket��ü�� ���ڷ� ClientHandler�� ��ü�� ����.
			ClientHandler chandler = new ClientHandler(client);
			// Clienthandler�� init() �޼ҵ带 ȣ��.
			chandler.init();
			}
		}*/
	}
}

