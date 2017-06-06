package IWannaEat.Handler;

import java.io.*;
import java.net.*;
import java.util.*;

import IWannaEat.info.Member;

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
				// ȸ�� ���� ���� String	
					String success = "fail"; // ���� ����
					String type = null;	//�մ�, ����
					String path = null; // ���� ���
					String fList []; // ���� ����Ʈ
					boolean check = false;
					
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
					// ������ Ŭ���̾�Ʈ�� ������ ������ �ش� id.txt���Ͽ� ��ü�� ����
						try{
							// id.txt������ �ִ��� �Ǵ� ����
							File dir = new File("./src/IWannaEat/info/id");
							//���丮�� ������ ����
							if(dir.exists()==false){
								dir.mkdir();
							}
							//���丮 �ȿ� �ִ� ����Ʈ ��������
							fList =dir.list();
							
							String pt =member.getId()+".txt";
							for(int i=0;i<fList.length;i++){
								if(pt.equals(fList[i])){
									//��ϵ� ����ڸ� true��ȯ �̵�� ����ڸ� false
									check=true;
								}
							}
							
							// check == false ��, ȸ������ �ȵ� id�� ���Խ�����
							if (!check){
								path = "./src/IWannaEat/info/id/" + member.getId() + ".txt";
								System.out.println(member.getId() + ".txt ����");
								ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(path));
								outmessage = success = "success";
								oos1.writeObject(member);
								oos1.close();
							}
							else{
								//TODO: �̹� ������ ID�Դϴ�.
							}
						}catch (EOFException ee){
							System.out.println("��� ��Ʈ���� �ݾ��ּ���.");
							ee.printStackTrace();
						}catch (IOException ie) {
							ie.printStackTrace();
						}
						System.out.println("data out");
						dataOut.writeUTF(outmessage);
						// ��½�Ʈ���� ���۸� ��� �ٷ� ����
						dataOut.flush();
					}
					else if (action.equals("�α���")){
						String id = stk.nextToken();
						String password = stk.nextToken();
						File rdir = new File("./src/IWannaEat/info/uplist"); //�α����� ���� ����Ʈ ���� ���丮
						
						if(rdir.exists()==false){
							rdir.mkdir();
						}
						path = "./src/IWannaEat/info/id/" + id + ".txt";
						
						
						try{
							ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
							Member member = (Member)ois.readObject();
							//TODO : Ÿ���� restaurant ���� client������ ���� �����ִ� ������ �ٸ��� ó��
							if(member.getId().equals(id) && member.getPassword().equals(password)){
								type = member.getType();
								success = "success";
								outmessage = success + "|" + type +"|" + member.getName();
								System.out.println(success + " : " + outmessage);
								// ������ ��� ����Ʈ�� ��� --> ȸ���� �� �� �ִ� ����Ʈ
								// TODO : name���� ������ ��� �ҷ����� ���� �� �Ǵ������� ����
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
							System.out.println(e + "������ ã�� �� �����ϴ�.");
							//TODO : ȸ�������� ���ּ���
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
	
	public void change_handler(Socket socket, String type, String id){
		// Ÿ�Կ� ���� ������ �Ű������� �ش� �ڵ鷯 ��ü ����
		if(type.equals("Restaurant")){
			//RestaurantHandler rhandler = new RestaurantHandler(socket, id);
			// Restauranthandler�� init() �޼ҵ带 ȣ��.
			//rhandler.init();
		}
		else if(type.equals("Guest")){
			GuestHandler ghandler = new GuestHandler(socket, id);
			// Guesthandler�� init() �޼ҵ带 ȣ��.
			ghandler.init();
		}
	}
}

