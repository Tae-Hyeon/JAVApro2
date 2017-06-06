package ps;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import javax.swing.*;
import java.awt.*;

class Setmain implements ActionListener,Runnable{

	JFrame CuF;
	JPanel CuNP;
	JLabel CuL;
	String shapname;
	JPanel CuT;
	JButton setTable [];
	
	JPanel CuB;
	ImageIcon msg;
	JButton msgB;
	
	JPanel CuBT;
	JButton end;
	int TCount [];

	String Tname;
	int Table;
	int btime;
	int TInfo[];
	private Thread listener;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	Socket socket;
	
	String sid;
	
	public Setmain(String id,String name, int table, int breaktime,int Tinfo[]) throws Exception{
		btime=breaktime;
		Table=table;
		sid=id;
		shapname=name;
		CuF = new JFrame();
		CuF.setLayout(new GridLayout(4,1));
		CuF.setSize(500, 500);
		CuNP = new JPanel();
		CuL = new JLabel(name);
		CuNP.add(CuL);
		CuF.add(CuNP);
		int n= table*table;
		CuT = new JPanel(new GridLayout(table,table));
		setTable=new JButton[n];

		for(int j=0;j<n;j++){
			setTable[j] = new JButton();
				if(Tinfo[j]==1){
				setTable[j].setBackground(new Color(255,0,0));
			}else if(Tinfo[j]==2){
				setTable[j].setBackground(new Color(0,0,255));
			}else if(Tinfo[j]==3){
				setTable[j].setBackground(new Color(0,0,0));
			}else{
				setTable[j].setBackground(new Color(255,255,255));
			}
		}
		
		for(int j=0; j<n;j++){
			CuT.add(setTable[j]);
		}
		
		CuF.add(CuT);
		System.out.println("ihihihi5");
		CuB = new JPanel();
		msg = new ImageIcon("msg.jpg");
		msgB = new JButton(msg);
		CuB.add(msgB);
		CuF.add(CuB);

		System.out.println("ihihihi6");
		CuBT = new JPanel();
		end = new JButton("확인");
		CuBT.add(end);
		CuF.add(CuBT);

		System.out.println("ihihihi7");
		CuF.setVisible(true);
		
	}
	public synchronized void start() throws IOException{
		if(listener==null){
			try{
				socket = new Socket("127.0.0.1",8080);
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			//이정보는 왜 안가는 거지?
			} catch(IOException ie){}
			
			listener = new Thread(this);
			listener.start();
		}
	}
	public synchronized void stop() throws IOException{
		if(listener!=null){
			listener.interrupt();
			listener=null;
			dataOut.close();
		}
	}


	@Override
	public void run() {
		try{
			while(!Thread.interrupted()){
				String line = dataIn.readUTF();
				StringTokenizer aa = new StringTokenizer(line, "|");
				String type =aa.nextToken();
				if(type.equals("가게테이블")){

					System.out.println("여긴가");
					int i=0;
					Tname=aa.nextToken();
					if(Tname.equals(shapname)){
						while(aa.hasMoreTokens()){
							TInfo[i]=Integer.parseInt(aa.nextToken());
							i++;
						}
						for(int j=0;j<TInfo.length;j++){
							setTable[j] = new JButton();
								if(TInfo[j]==1){
								setTable[j].setBackground(new Color(255,0,0));
							}else if(TInfo[j]==2){
								setTable[j].setBackground(new Color(0,0,255));
							}else if(TInfo[j]==3){
								setTable[j].setBackground(new Color(0,0,0));
							}else{
								setTable[j].setBackground(new Color(255,255,255));
							}
						}

						new Setmain(sid,Tname,Table,btime,TInfo);
				}
					
					
				}
			
			}	
			
		}catch(Exception e){}
		
		
		
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource()==end){
			CuF.dispose();
		}
	}
}

public class CustomerClient extends JFrame implements Runnable,ActionListener{
	String id;
	String Shap;
	
	JFrame CL;
	JPanel bab;
	JPanel bt;
	JButton ok;
	JButton x;
	JLabel lab;
	JComboBox Blist;
	Socket socket;
	private Thread listener;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	
	String basename;
	int basetable;
	int basebreak;
	
	String Tname;
	int TInfo[];
	public CustomerClient(String line) throws Exception{
		System.out.println(line);
		CL = new JFrame();
		CL.setLayout(new GridLayout(2,1));
		CL.setSize(500, 500);
		StringTokenizer check = new StringTokenizer(line,"|");
	
		check.nextToken();
		check.nextToken();
		id= check.nextToken();
		check.nextToken();
		String Slist[]= new String [check.countTokens()];
		int i=0;
		while(check.hasMoreTokens()){
			
			Slist[i]=check.nextToken();
			System.out.println(Slist[i]);
			i++;
		}
		
		
		bab = new JPanel();
		lab = new JLabel("밥 집");
		Blist = new JComboBox();
		for(int j=0; j<Slist.length;j++){		
			Blist.addItem(Slist[j]);
		}
		bab.add(lab);
		bab.add(Blist);
		CL.add(bab);
		
		bt = new JPanel();
		ok = new JButton("확인");
		x = new JButton("취소");
		ok.addActionListener(this);
		x.addActionListener(this);
		bt.add(ok);
		bt.add(x);
		CL.add(bt);
		
		CL.setVisible(true);

		start();
	}
	
	
	

	public synchronized void start() throws IOException{
		if(listener==null){
			try{
				socket = new Socket("127.0.0.1",8080);
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			//이정보는 왜 안가는 거지?
			} catch(IOException ie){}
			
			listener = new Thread(this);
			listener.start();
		}
	}
	public synchronized void stop() throws IOException{
		if(listener!=null){
			listener.interrupt();
			listener=null;
			dataOut.close();
		}
	}
	public void run(){
		try{
			while(!Thread.interrupted()){
				String line = dataIn.readUTF();
				StringTokenizer aa = new StringTokenizer(line, "|");
				String type =aa.nextToken();
				if(type.equals("가게기본정보")){
					StringTokenizer base = new StringTokenizer(line, "|");
					base.nextToken();
					base.nextToken();
					basename = base.nextToken();
					basetable = Integer.parseInt(base.nextToken());
					basebreak = Integer.parseInt(base.nextToken());
					TInfo= new int [(basetable*basetable)];
					System.out.println();
					new Setmain(id,basename,basetable,basebreak,TInfo);
				}else if(type.equals("가게테이블")){

					System.out.println("여긴가2");
					int i=0;
					Tname=aa.nextToken()+".txt";
					if(Tname.equals(Shap)){
						while(aa.hasMoreTokens()){
							TInfo[i]=Integer.parseInt(aa.nextToken());
							i++;
						}
						System.out.println(TInfo.length);
						new Setmain(id,basename,basetable,basebreak,TInfo);
					}
			
				}
			}
			
			}catch(Exception e){}

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==Blist){
			Shap=(String) Blist.getSelectedItem();
		}
		
		if(e.getSource()==ok){
			
			try {
				Shap=(String)Blist.getSelectedItem();
				dataOut.writeUTF("정보좀"+"|"+id+"|"+Shap);
				dataOut.flush();
				try {
					new Setmain(id,basename,basetable,basebreak,TInfo);
				} catch (Exception e1) {
				}
				CL.dispose();
				

			} catch (IOException e1) {}
			
		}
		
		if(e.getSource()==x){
			CL.dispose();
		}
		
		
		
	}
}
