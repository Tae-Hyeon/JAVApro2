package ps;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

/**
 * class : PizzaClient
 * JFrame�� ��ӹް� , ActionListener �������̽��� ���� ���߻���� ������ �̿�
 * ���ø��� �ƴ� �Ϲ� �������α׷��̱� ������ �����ϱ� ���ؼ��� main()�޼ҵ带 ȣ���Ͽ� ����. 
 */

public class PizzaClient extends JFrame implements ActionListener {
	//size �� taste ���� JRadioButton���� �׷�ȭ �ϱ����� ButtonGroup ����
	//ButtonGroup ���� RadionButton�� �ϳ��� ���� ����
	private ButtonGroup jbg1;
	private ButtonGroup jbg2;

	//������ ������ ���� JRadioButton ����
	private JRadioButton jrb1;
	private JRadioButton jrb2;
	private JRadioButton jrb3;
	private JRadioButton jrb4;
	private JRadioButton jrb5;
	private JRadioButton jrb6;

	//Ŭ����ư�� �ϱ� ���� JButton ����
	private JButton button1;

	//JRadioButton, JTextField, JButton, JLabel�� ��� ������ �׸��� JPanel ����
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	private JPanel panel5;

	//Name, Address, Phone Number�� �Է��ϱ� ���� JTextField ����
	private JTextField textField1;
	private JTextField textField2;
	private JTextField textField3;

	private JLabel label1;

	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private static PizzaClient pca;
	
	//���õ� ������ư�� text���� �ֱ� �����ϱ� ���� String ���� ����
	private String size;
	private String taste;

	public PizzaClient() {

		//ButtonGroup�� ��� ������ ��ü(�������̴�)�� �ƴ� ���� ��ü
		jbg1 = new ButtonGroup();
		jbg2 = new ButtonGroup();

		//������ JRadioButton�� �����ϰ� ó���� ���õǾ� ���� �ʵ��� false�� ����
		jrb1 = new JRadioButton("Small", false);
		jrb2 = new JRadioButton("Medium", false);
		jrb3 = new JRadioButton("Large", false);
		jrb4 = new JRadioButton("Veggies", false);
		jrb5 = new JRadioButton("Meat", false);
		jrb6 = new JRadioButton("California", false);

		//������ JRadioButton�� ButtonGroup�� �׷�ȭ
		jbg1.add(jrb1);
		jbg1.add(jrb2);
		jbg1.add(jrb3);

		jbg2.add(jrb4);
		jbg2.add(jrb5);
		jbg2.add(jrb6);

		//JTextField�� ��Ÿ���� �� ����
		textField1 = new JTextField("Name");
		textField2 = new JTextField("Address");
		textField3 = new JTextField("Phone Number");

		//JTextField�� ��Ʈ ����
		//Font Ŭ������ �����ڴ� font�̸�, font ��Ÿ��, font ũ�� ������ ����
		//���� : font �̸����� Monospaced, Serif, SansSerif ���� ����
		//���� : font ��Ÿ�Ͽ��� BOLD, ITALIC ���� ����
		textField1.setFont(new Font("SansSerif", Font.BOLD, 12));
		textField2.setFont(new Font("SansSerif", Font.BOLD, 12));
		textField3.setFont(new Font("SansSerif", Font.BOLD, 12));

		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		panel5 = new JPanel();

		button1 = new JButton();

		label1 = new JLabel();

		//panel1�� ���̾ƿ��� �����ϰ� panel1�ȿ����� ������ 10,10���� ����
		panel1.setLayout(new BorderLayout(10,10));
		panel1.add(textField1, BorderLayout.NORTH);
		panel1.add(textField2, BorderLayout.CENTER);
		panel1.add(textField3, BorderLayout.SOUTH);
		add(panel1, BorderLayout.NORTH);

		//panel2�� ���̾ƿ��� �����ϰ� panel4�� panel5�� panel2�ȿ� ����
		panel2.setLayout(new GridLayout());
		panel4.setLayout(new GridLayout(3, 1));
		panel4.setBackground(new Color(204, 204, 255));
		panel2.add(panel4);
		panel5.setLayout(new GridLayout(3, 1));
		panel5.setBackground(new Color(204, 204, 255));
		panel2.add(panel5);

		//panel4�� size�� ���õ� JRadioButton �� JTextField ����
		//ButtonGroup�� ���� ��ü�̱� ������ ���� ���� �ʾƵ� ��
		panel4.add(jrb1);
		panel4.add(jrb2);
		panel4.add(jrb3);


		//panel5�� taste�� ���õ� JRadioButton �� JTextField ����
		//ButtonGroup�� ���� ��ü�̱� ������ ���� ���� �ʾƵ� ��
		panel5.add(jrb4);
		panel5.add(jrb5);
		panel5.add(jrb6);

		add(panel2, BorderLayout.CENTER);
		panel3.setLayout(new BorderLayout());

		//button1.setLabel �̶�� ǥ�� �����ϳ� 
		//jdk 5.0���� ��õ���� �ʴ� �޼ҵ��̱� ������ button1.setText �� ��ü
		button1.setText("Submit");

		//button1�� Ŭ���Ҷ� �߻��ϴ� �̺�Ʈ�� addActtionListener�� �߰�
		button1.addActionListener(this);
		panel3.add(button1, BorderLayout.CENTER);

		label1.setBackground(new Color(153, 153, 255));
		label1.setText("Information");
		panel3.add(label1, BorderLayout.SOUTH);
		add(panel3, BorderLayout.SOUTH);

		try {
			// ip, port�� ���ڷ� Socket�� ��ü�� ����
			// ����� ȯ�濡 ���� ip�� port���� �����ؼ� ���
			socket = new Socket("127.0.0.1", 8080);
			// �������κ��� �Է½�Ʈ���� ȹ��
		dataIn = new DataInputStream(new BufferedInputStream(socket
					.getInputStream()));
			// �������κ��� ��½�Ʈ���� ȹ��
		dataOut = new DataOutputStream(new BufferedOutputStream(socket
					.getOutputStream()));
		} catch (IOException ie) {
			stop();
		}
	}

	
	/*
	 * method : actionPerformed() 
	 * �̺�Ʈ ó���� ���� �޼ҵ� 
	 * submit ��ư�� ����ԵǸ� label1(���¶�)�� 'Sending Order...'�� �ٲ��� 
	 * '|'�� �����ڷ� �ؼ� ������� ������ �ֹ������� ������������
	 *label1�� 'Thank you!'�� �ٲ㼭 �ֹ��� �ϷḦ �˸�
	 */

	public void actionPerformed(ActionEvent event) {
		//jrb1~jrb3�߿��� ���õ� ��ư�� Text���� size ������ ���� ���� ���
		if(jrb1.isSelected()){
			size = jrb1.getText();
		}
		else if(jrb2.isSelected()){
			size = jrb2.getText();
		}
		else if(jrb3.isSelected()){
			size = jrb3.getText();
		}
		//jrb4~jrb6�߿��� ���õ� ��ư�� Text���� taste ������ ���� ���� ���
		if(jrb4.isSelected()){
			taste = jrb4.getText();
		}
		else if(jrb5.isSelected()){
			taste = jrb5.getText();
		}
		else if(jrb6.isSelected()){
			taste = jrb6.getText();
		}
		
		// �߻��� ActionEvent�� ActionCommnad(���⼭�� ��ư�� text)�� 		"Submit"�� ������ ����
		if (event.getActionCommand().equals("Submit")) {
			label1.setText("Sending Order...");
			try {
				// ��½�Ʈ���� �ֹ������� ��|���� �����ڷ� ���
				dataOut.writeUTF(textField1.getText() + "|"
				+ textField2.getText() + "|" 
				+ textField3.getText() + "|" + size + "|"+ taste);
				// �ֹ��� �Ϸ�Ǿ����� ���̺��� 
				//�ؽ�Ʈ ���� �����ؼ� �˸�
				label1.setText("Thank you!");
				// ��½�Ʈ���� ���۸� ��� �ٷ� ����
				dataOut.flush();
			} catch (Exception e) {
				// ���ܰ� �߻��ϸ� ������ ���¸� ���̺� ǥ��
				label1.setText("Error : " + e.toString());
			}
		}
	}

	/*
	 * method : stop() 
	 * ��� ������ ���� ������ ���� 
	 * ������ IOException�� ó���ϱ� ���ؼ� ȣ�� ����
	 */
	public void stop() {
		try {
			dataIn.close();
			dataOut.close();
			socket.close();

		} catch (IOException e) {
			label1.setText("Error : " + e.toString());
		}
	}

	public static void main(String[] args) {
		pca = new PizzaClient();
		pca.setTitle("Pizza");
		pca.setSize(200, 350);

		//������ â�� X��ư�� �����ÿ� �ش�Ǵ� �̺�Ʈ ����
		//�̺�Ʈ�� �������� �����ÿ� ���������� ������������ 
		//����Ǳ� ������ ����ó���� �߻�
		//���� �̺�Ʈ�� ���ؼ� stop() �޼ҵ带 ȣ��
		pca.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pca.stop();
			}
		});
		pca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pca.setVisible(true);

	}
}