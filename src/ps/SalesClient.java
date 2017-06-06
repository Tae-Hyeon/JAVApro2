package ps;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

class Optionsign implements ActionListener{
	private JFrame Po;
	
	private JPanel shapP;
	private JPanel tableP;
	private JPanel breakP;
	private JPanel buttonP;
	
	private JTextField shapname;
	private String Shap;
	private JComboBox tableset;
	private JComboBox breakset; 
	
	private JLabel shap;
	private JLabel tablel;
	private JLabel breakl;
	
	private String tablecount[]={"3x3","4x4","5x5"};
	private String breaktime []={"2~3시","3~4시","4~5시"};
	
	private int table;
	private int bt;
	
	JButton ok;
	JButton no;
	String did;
	DataOutputStream dataOut;
	String pass="가게정보"+"|";
	String dshap;
	int dtable;
	int dbt;
	Socket socket;
	public Optionsign(String id) throws Exception{
		did=id;
		//프레임설정
		Po = new JFrame("BAB");
		Po.setSize(500, 500);
		Po.setLayout(new GridLayout(4,1));
		
		//상호명 설정
		shapP = new JPanel();
		shap = new JLabel("상호명");
		shapname = new JTextField(15);
		shapP.add(shap);
		shapP.add(shapname);
		Po.add(shapP);
		
		//테이블 개수 설정
		tableP = new JPanel();
		tablel = new JLabel("테이블 개수 : ");
		tableset = new JComboBox();
		for(int i=0;i<3;i++){
			tableset.addItem(tablecount[i]);
		}
		tableset.addActionListener(this);
		tableP.add(tablel);
		tableP.add(tableset);
	    Po.add(tableP);
	    
	    //브레이트 타임 설정
	    breakP = new JPanel();
		breakl = new JLabel("브레이크 타임 : ");
		breakset = new JComboBox();
		for(int i=0;i<3;i++){
			breakset.addItem(breaktime[i]);
		}
		breakset.addActionListener(this);
		breakP.add(breakl);
		breakP.add(breakset);
		Po.add(breakP);
		
		//버튼 설정
		buttonP = new JPanel();
		ok = new JButton("확인");
		no = new JButton("취소");
		ok.addActionListener(this);
		no.addActionListener(this);
		buttonP.add(ok);
		buttonP.add(no);
		Po.add(buttonP);
		
		Po.setVisible(true);
		
		
	}
	private void start() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//테이블 설정
		if(e.getSource()==tableset){
			if(tableset.getSelectedItem().equals("3x3")){
				table=3;
			}else if(tableset.getSelectedItem().equals("4x4")){
				table=4;
			}else if(tableset.getSelectedItem().equals("5x5")){
				table=5;
			}
		
		}
		//브레이크타임 설정
		if(e.getSource()==breakset){
			if(breakset.getSelectedItem().equals("2~3시")){
				bt=2;
			}else if(breakset.getSelectedItem().equals("3~4시")){
				bt=3;
			}else if(breakset.getSelectedItem().equals("4~5시")){
				bt=4;
			}
		}
		
		//ok버튼이 눌렸을때
		if(e.getSource()==ok){
			try {
				socket= new Socket("127.0.0.1",8080);
				dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				pass=pass+did+"|"+shapname.getText()+"|"+table+"|"+bt;
				dataOut.writeUTF(pass);
				dataOut.flush();
				Po.dispose();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		if(e.getSource()==no){
			//no버튼 누르면 걍 창닫기
			Po.dispose();
		}
	}
	
	public int gettable(){
		return dtable;
	}
	public int getbreak(){
		return dbt;
	}
	public String getShapname(){
		return dshap;
	}
}
public class SalesClient implements ActionListener, Runnable {
	String id;
	String passwd;
	String state;
	String name;
	String out="가게테이블";
	String countT [];
	String pass="가게정보"+"|";
	String upload="가게업로드"+"|";
	boolean check=false;
	JFrame SC;
	JPanel tableP;
	JPanel nameP;
	JPanel msgP;
	JPanel btP;
	JButton xB;
	JButton msgB;
	JButton su;
	JButton [] jb;
	JLabel shapname;
	ImageIcon msg;
	
	int i=0;
	int count [] ;
	int t=0;
	int bt=0;
	int suCount=0;
	
	Socket socket;
	DataInputStream dataIn;
	DataOutputStream dataOut;
	
	
	public SalesClient(String line) throws Exception{
		

		StringTokenizer base = new StringTokenizer(line,"|");
		base.nextToken();
		base.nextToken();
		id=base.nextToken();
		state = base.nextToken();
		if(state.equals("옵션정보없음")){
			new Optionsign(id);
			//밑에 판이 안뜸... 저기 옵션 추가하는 것에서 끝...ㅠ

		}else if(state.equals("옵션정보있음")){
			name=base.nextToken();
			t=Integer.parseInt(base.nextToken());
			bt=Integer.parseInt(base.nextToken());
		}
		
		upload=upload+name+"|"+id;
		
		SC = new JFrame();
		SC.setLayout(new GridLayout(4,1));
		SC.setSize(500, 500);
		
		//맨 상단에 이름써주기
		nameP = new JPanel();
		shapname = new JLabel(name);
		nameP.add(shapname);
		SC.add(nameP,BorderLayout.NORTH);
		
		// 테이블 갯수 불러오기
		int n= t*t;
		//테이블 판 등록
		tableP = new JPanel();
		tableP.setLayout(new GridLayout(t,t)); //테이블 바둑판 설정!
		jb = new JButton [n]; // 버튼 n 개 만들기
		count = new int [n]; // 카운트도 n개 만들기
		//count reset , 버튼 등록, 초기 버튼 색깔 빨간색
		for(;i<n;i++){
			count[i]=0;
			jb[i] = new JButton ();
			jb[i].setBackground(new Color(255,255,255));
			jb[i].addActionListener(this);
		}
		//판넬에 버튼 다 집어 넣기
		for(int j=0; j<n;j++){
			jb[j].setEnabled(false);
			tableP.add(jb[j]);
		}
		SC.add(tableP);
		
		//메세지 판 생성
		msgP = new JPanel();
		msg = new ImageIcon("msg.jpg");
		msgB = new JButton(msg);
		msgP.add(msgB);
		SC.add(msgP);

		btP = new JPanel();
		
		//버튼판 생성
		su = new JButton("수정");
		xB = new JButton("확인");
		xB.addActionListener(this);
		su.addActionListener(this);
		btP.add(su);
		btP.add(xB);
		SC.add(btP);

		start();
		
		
		SC.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		for(int j=0; j<count.length;j++){
			//count별로 버튼 색깔 표시 1이면 빨간색, 2이면 파란색, 3이면 검정색 표시
			if(e.getSource()==jb[j]){
				if(count[j]==3){
					count[j]=0;
				}
				++count[j];
				if(count[j]==1){
					jb[j].setBackground(new Color(255,0,0));
				}else if(count[j]==2){
					jb[j].setBackground(new Color(0,0,255));
				}else if(count[j]==3){
					jb[j].setBackground(new Color(0,0,0));
				}
			}
		}
		if(e.getSource()==xB){
			//취소를 누르면 모든 것을 종료
			try{
				String ss ="가게종료"+"|"+name;
				dataOut.writeUTF(ss);
				dataOut.flush();
				SC.dispose();
				
				dataOut.close();
				socket.close();
			}catch (IOException f){}
		}
		if(e.getSource()==su){
			//수정 버튼을 눌렀을 때 1번 누르면 버튼 수정가능 2번누르면 버튼 수정 불가
			if(suCount==2){
				suCount=0;
			}
			++suCount;
			if(suCount==2){
				// 2번 눌렀을 때 수정 불가로 만들고 정보를 전송하기.
				for(int j =0;j<count.length;j++){
					jb[j].setEnabled(false);
				}
				out=out+"|"+name;
				for(int k=0;k<count.length;k++){
					out=out+"|"+String.valueOf(count[k]);
				}
				
				try{
					System.out.println(out);
				dataOut.writeUTF(out);
				dataOut.flush();
				out="가게테이블";
				}catch(IOException f){}
			}else if(suCount==1){
				for(int j =0;j<count.length;j++){
					jb[j].setEnabled(true);
				}
			}
		}
	}
	Thread listener;
	
	public synchronized void start() throws IOException{
		if(listener==null){
			try{
				socket = new Socket("127.0.0.1",8080);
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			dataOut.writeUTF(upload);
			dataOut.flush();
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
				if(line.startsWith("가게정보")){
				}else if(line.startsWith("로그인 완료")){
					new SalesClient(line);
				}
				
			}
		}catch(Exception e){}
	}
}