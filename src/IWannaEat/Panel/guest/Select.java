package IWannaEat.Panel.guest;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import IWannaEat.main.InitClient;
import IWannaEat.Panel.guest.GuestPane;

public class Select extends JPanel implements ActionListener{
	private InitClient Init;
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private JPanel mpn;
	
	private JPanel cbp;
	private JPanel btp;
	
	private JLabel label;
	private JComboBox combo;
	
	private JButton jbt1;
	private JButton jbt2;
	private JButton jbt3;
	
	public Select(InitClient init, Socket socket) {
		try {
			this.socket = socket;
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
		
		//cardlayout�� �ֱ� ���� InitClient�� �޴´�
		Init = init;
		
		label = new JLabel();
		label.setText("������� �Ĵ��� �����ϼ���!!");
		
		//���� ����Ʈ�� ���� ComboBox
		combo = new JComboBox();
		uploadList(combo);
		
		//���Ը���Ʈ reupload, �ش簡�� ����, �α׾ƿ��� ���� ��ư ����
		jbt1 = new JButton("���ΰ�ħ");
		jbt1.addActionListener(this);
		jbt2 = new JButton("����");
		jbt2.addActionListener(this);
		jbt3 = new JButton("�α׾ƿ�");
		jbt3.addActionListener(this);
	
		// �޺��ڽ��� �� �г�
		cbp = new JPanel();
		cbp.setLayout(new GridLayout(3, 1, 0, 10));
		cbp.add(label);
		cbp.add(combo);
		cbp.add(jbt1);
		combo.addActionListener(this);
		
		// ����, �α׾ƿ� ��ư�� �� �г�
		btp = new JPanel();
		btp.setLayout(new GridLayout(1, 2, 10, 10));
		btp.add(jbt2);
		btp.add(jbt3);
		
		//���� �гο� �� �г� �߰�
		mpn = new JPanel();
		mpn.setLayout(new BorderLayout(20,20));
		mpn.add(cbp, BorderLayout.NORTH);
		mpn.add(btp, BorderLayout.SOUTH);
		
		add(mpn);
		
		setSize(500, 700);

		setVisible(true);
	}
	
	public synchronized void uploadList(JComboBox cb) {
		try {
			//�ڵ鷯�κ��� list�� �޾� ��ū�� �̿��� �����ϰ� comboBox�� �߰��Ѵ�.
			dataOut.writeUTF("upload");
			dataOut.flush();
			String message = dataIn.readUTF();
			String item = null;
			StringTokenizer stk = new StringTokenizer(message, "|");
			int num = stk.countTokens();
			check:
			for (int i = 0; i < num; i++){
				item = stk.nextToken();
	            for(int j = 0; j < combo.getItemCount() ; j++)
	            {
	            	System.out.println(item +" : "+ combo.getItemAt(j).toString());
		            String str = combo.getItemAt(j).toString();
	                if(item.compareTo(str) == 0)
	                {
	                    //�׸��� ���� �̸��� ������ �߰����� �ʽ��ϴ�
	                    continue check;
	                }
	            }
               	combo.addItem(item); //��ġ�ϴ� �׸��� ������ �Է��� ������ �޺��ڽ� �׸� �߰��մϴ�
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
	public void stop(){
		try {
			dataIn.close();
			dataOut.close();
			socket.close();

		} catch (IOException e) {
			System.out.println("Error : " + e.toString());
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String message = null;
		if(event.getActionCommand().equals("���ΰ�ħ"))
        {
            uploadList(combo);
        }
		else if(event.getActionCommand().equals("����"))
        {
            //TODO : ���� ȭ�� cardlayout�� �߰� �� ����
			String restaurant = combo.getSelectedItem().toString(); 
			message = "select" + "|" + restaurant;
			try {
				dataOut.writeUTF(message);
				dataOut.flush();
				Init.getContentPane().add(restaurant, new GuestPane(Init, socket));
				Init.getCardLayout().show(Init.getContentPane(), restaurant);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		else if(event.getActionCommand().equals("�α׾ƿ�"))
        {
            //TODO : �α׾ƿ� ( �г� dispose?? cardlayout���� ����, loginpane���� �̵�)
			Init.dispose();
			System.out.println("logout");
			InitClient inc = new InitClient();
        }
	}
}
