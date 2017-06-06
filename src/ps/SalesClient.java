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
	private String breaktime []={"2~3��","3~4��","4~5��"};
	
	private int table;
	private int bt;
	
	JButton ok;
	JButton no;
	String did;
	DataOutputStream dataOut;
	String pass="��������"+"|";
	String dshap;
	int dtable;
	int dbt;
	Socket socket;
	public Optionsign(String id) throws Exception{
		did=id;
		//�����Ӽ���
		Po = new JFrame("BAB");
		Po.setSize(500, 500);
		Po.setLayout(new GridLayout(4,1));
		
		//��ȣ�� ����
		shapP = new JPanel();
		shap = new JLabel("��ȣ��");
		shapname = new JTextField(15);
		shapP.add(shap);
		shapP.add(shapname);
		Po.add(shapP);
		
		//���̺� ���� ����
		tableP = new JPanel();
		tablel = new JLabel("���̺� ���� : ");
		tableset = new JComboBox();
		for(int i=0;i<3;i++){
			tableset.addItem(tablecount[i]);
		}
		tableset.addActionListener(this);
		tableP.add(tablel);
		tableP.add(tableset);
	    Po.add(tableP);
	    
	    //�극��Ʈ Ÿ�� ����
	    breakP = new JPanel();
		breakl = new JLabel("�극��ũ Ÿ�� : ");
		breakset = new JComboBox();
		for(int i=0;i<3;i++){
			breakset.addItem(breaktime[i]);
		}
		breakset.addActionListener(this);
		breakP.add(breakl);
		breakP.add(breakset);
		Po.add(breakP);
		
		//��ư ����
		buttonP = new JPanel();
		ok = new JButton("Ȯ��");
		no = new JButton("���");
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
		//���̺� ����
		if(e.getSource()==tableset){
			if(tableset.getSelectedItem().equals("3x3")){
				table=3;
			}else if(tableset.getSelectedItem().equals("4x4")){
				table=4;
			}else if(tableset.getSelectedItem().equals("5x5")){
				table=5;
			}
		
		}
		//�극��ũŸ�� ����
		if(e.getSource()==breakset){
			if(breakset.getSelectedItem().equals("2~3��")){
				bt=2;
			}else if(breakset.getSelectedItem().equals("3~4��")){
				bt=3;
			}else if(breakset.getSelectedItem().equals("4~5��")){
				bt=4;
			}
		}
		
		//ok��ư�� ��������
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
			//no��ư ������ �� â�ݱ�
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
	String out="�������̺�";
	String countT [];
	String pass="��������"+"|";
	String upload="���Ծ��ε�"+"|";
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
		if(state.equals("�ɼ���������")){
			new Optionsign(id);
			//�ؿ� ���� �ȶ�... ���� �ɼ� �߰��ϴ� �Ϳ��� ��...��

		}else if(state.equals("�ɼ���������")){
			name=base.nextToken();
			t=Integer.parseInt(base.nextToken());
			bt=Integer.parseInt(base.nextToken());
		}
		
		upload=upload+name+"|"+id;
		
		SC = new JFrame();
		SC.setLayout(new GridLayout(4,1));
		SC.setSize(500, 500);
		
		//�� ��ܿ� �̸����ֱ�
		nameP = new JPanel();
		shapname = new JLabel(name);
		nameP.add(shapname);
		SC.add(nameP,BorderLayout.NORTH);
		
		// ���̺� ���� �ҷ�����
		int n= t*t;
		//���̺� �� ���
		tableP = new JPanel();
		tableP.setLayout(new GridLayout(t,t)); //���̺� �ٵ��� ����!
		jb = new JButton [n]; // ��ư n �� �����
		count = new int [n]; // ī��Ʈ�� n�� �����
		//count reset , ��ư ���, �ʱ� ��ư ���� ������
		for(;i<n;i++){
			count[i]=0;
			jb[i] = new JButton ();
			jb[i].setBackground(new Color(255,255,255));
			jb[i].addActionListener(this);
		}
		//�ǳڿ� ��ư �� ���� �ֱ�
		for(int j=0; j<n;j++){
			jb[j].setEnabled(false);
			tableP.add(jb[j]);
		}
		SC.add(tableP);
		
		//�޼��� �� ����
		msgP = new JPanel();
		msg = new ImageIcon("msg.jpg");
		msgB = new JButton(msg);
		msgP.add(msgB);
		SC.add(msgP);

		btP = new JPanel();
		
		//��ư�� ����
		su = new JButton("����");
		xB = new JButton("Ȯ��");
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
			//count���� ��ư ���� ǥ�� 1�̸� ������, 2�̸� �Ķ���, 3�̸� ������ ǥ��
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
			//��Ҹ� ������ ��� ���� ����
			try{
				String ss ="��������"+"|"+name;
				dataOut.writeUTF(ss);
				dataOut.flush();
				SC.dispose();
				
				dataOut.close();
				socket.close();
			}catch (IOException f){}
		}
		if(e.getSource()==su){
			//���� ��ư�� ������ �� 1�� ������ ��ư �������� 2�������� ��ư ���� �Ұ�
			if(suCount==2){
				suCount=0;
			}
			++suCount;
			if(suCount==2){
				// 2�� ������ �� ���� �Ұ��� ����� ������ �����ϱ�.
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
				out="�������̺�";
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
				if(line.startsWith("��������")){
				}else if(line.startsWith("�α��� �Ϸ�")){
					new SalesClient(line);
				}
				
			}
		}catch(Exception e){}
	}
}