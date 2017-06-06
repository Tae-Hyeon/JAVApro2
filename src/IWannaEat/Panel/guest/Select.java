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
	
	public Select(InitClient init) {
		try {
			socket = new Socket("127.0.0.1", 8080);
			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
		
		//cardlayout에 넣기 위해 InitClient를 받는다
		Init = init;
		
		label = new JLabel();
		label.setText("보고싶은 식당을 선택하세요!!");
		
		//가게 리스트를 위한 ComboBox
		combo = new JComboBox();
		uploadList(combo);
		
		//가게리스트 reupload, 해당가게 선택, 로그아웃을 위한 버튼 생성
		jbt1 = new JButton("새로고침");
		jbt1.addActionListener(this);
		jbt2 = new JButton("선택");
		jbt2.addActionListener(this);
		jbt3 = new JButton("로그아웃");
		jbt3.addActionListener(this);
	
		// 콤보박스가 들어갈 패널
		cbp = new JPanel();
		cbp.setLayout(new GridLayout(3, 1, 0, 10));
		cbp.add(label);
		cbp.add(combo);
		cbp.add(jbt1);
		combo.addActionListener(this);
		
		// 선택, 로그아웃 버튼이 들어갈 패널
		btp = new JPanel();
		btp.setLayout(new GridLayout(1, 2, 10, 10));
		btp.add(jbt2);
		btp.add(jbt3);
		
		//메인 패널에 각 패널 추가
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
			//핸들러로부터 list를 받아 토큰을 이용해 구분하고 comboBox에 추가한다.
			String message = dataIn.readUTF();
			StringTokenizer stk = new StringTokenizer(message, "|");
			int num = stk.countTokens();
			check:
			for (int i = 0; i < num; i++){
				//항목의 반환형이 오브젝트의 참조값으로 반환되니 String으로 형변환 해줘야 합니다
	            String str = (String)combo.getSelectedItem();
	            for(int j = 0; j < combo.getItemCount() ; j++)
	            {
	                if(((String)combo.getItemAt(j)).compareTo(str) == 0)
	                {
	                    //항목중 같은 이름이 있으면 추가하지 않습니다
	                    continue check;
	                }
	            }
	            combo.addItem(str); //일치하는 항목이 없으면 입력한 내용을 콤보박스 항목에 추가합니다
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
		if(event.getActionCommand().equals("새로고침"))
        {
            //TODO : handler에 스트림을 보내 pushList를 시키고 다시 패널에서 받아 uploadlist를 시킨다. 
        }
		else if(event.getActionCommand().equals("선택"))
        {
            //TODO : 가게 화면 cardlayout에 추가 및 들어가기
        }
		else if(event.getActionCommand().equals("로그아웃"))
        {
            //TODO : 로그아웃 ( 패널 dispose?? cardlayout에서 제거, loginpane으로 이동) 
        }
	}
}
