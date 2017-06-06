package ps;
/*
 * ChatClient.java
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * class : ChatClient 
 * Runnable �������̽��� ActionListener �������̽��� implements�ؼ� 
 * �����带 �����ϰ� �̺�Ʈ�� �ڵ鸵�Ѵ�.
 */
public class ChatClient implements Runnable, ActionListener {
    private Frame frame;
    private Button button1;
    private TextArea textArea1;
    private Label label1;
    private TextField textField1;
    private Panel panel2;
    private Button button2;
    private Panel panel1;
    private TextField textField2;
    private Label label2;
    private TextField textField3;
    private Label label3;
    private TextField textField4;
    protected String host;
    protected int port;
    protected String id;

    /**
     * ������ : ChatClient() 
     * �⺻���� ȭ�� ������ �Ѵ�.
     */
    public ChatClient() {
        frame = new Frame();
        panel1 = new Panel();
        label1 = new Label();
        textField1 = new TextField();
        label2 = new Label();
        textField2 = new TextField();
        label3 = new Label();
        textField3 = new TextField();
        button1 = new Button();
        button2 = new Button();
        panel2 = new Panel();
        textArea1 = new TextArea();
        textField4 = new TextField();

        //�����ӿ� WindowListener�� �߰��Ͽ� ������ ���� �̺�Ʈ�� �����Ѵ�.
        //WindowListener �������̽��� ��� ������ implements���� �ʰ� �ʿ���
        //�޼ҵ常�� ����ϱ� ���� WindowAdapter�� �̿��Ѵ�.
        frame.addWindowListener(new WindowAdapter() {
            /**
             * method : windowCloseing() 
             * Ȱ��ȭ �Ǿ����� ���� �ݱ� ��ư�� Ȱ��ȭ �����ش�.
             */
            public void windowClosing(WindowEvent e) {
                try {
                    stop();
                } catch (IOException ex) {
                }
                // �������� �ڿ��� �ݳ��ϰ� �ý����� �����Ѵ�.
                frame.dispose();
                System.exit(0);
            }
        });
        
        //���ο� �����찡 ������ �� ��Ŀ��ó���� ���� �����ʸ� �߰��Ѵ�.
        frame.addWindowListener(new WindowAdapter() {
            /**
             * method : windowOpened() �������� �߸� �ڵ������� label1�� ��Ŀ���� �����.
             */
            public void windowOpended(WindowEvent e) {
                label1.requestFocus();
            }
        });

        // panel1�� ���̾ƿ��� 4�� 2���� GridLayout���� ����
        panel1.setLayout(new GridLayout(4, 2));

        label1.setText("Server IP");
        panel1.add(label1);

        textField1.setColumns(20);
        panel1.add(textField1);

        label2.setText("Port");
        panel1.add(label2);

        textField2.setColumns(8);
        panel1.add(textField2);

        label3.setText("ID");
        panel1.add(label3);

        textField3.setColumns(20);
        panel1.add(textField3);

        button1.setLabel("Connect");
        // ��ư�� ������ ��� �̺�Ʈ ó���� ���� �����ʸ� ��ư�� �߰��Ѵ�.
        button1.addActionListener(this);
        panel1.add(button1);

        button2.setLabel("Disconnect");
        button2.addActionListener(this);
        panel1.add(button2);

        textField4.setText("input");
        textField4.setColumns(40);
        textField4.setEditable(false);
        textField4.addActionListener(this);
        
        // panel1�� BorderLayout�� frame�� NORTH�� �߰��Ѵ�.
        frame.add(panel1, BorderLayout.NORTH);

        panel2.setLayout(new BorderLayout());

        panel2.add(textArea1, BorderLayout.CENTER);

        panel2.add(textField4, BorderLayout.SOUTH);

        frame.add(panel2, BorderLayout.CENTER);
        // frame�� �ڽ��� ���� ������Ʈ�� ����� �°� �����Ѵ�.
        frame.pack();
        // frame�� ȭ�鿡 ���̰��Ѵ�.
        frame.setVisible(true);
    }

    protected DataInputStream dataIn;
    protected DataOutputStream dataOut;
    protected Thread listener;

    /**
     * �޼ҵ� : start() 
     * ä�� ������ ���� Socket���� �����Ѵ�. 
     * ����� �������κ��� ���۸� ��Ʈ���� dataIn�� dataOut�� �����Ѵ�. 
     * ������ listener�� �����Ͽ� �����κ����� �޽����� �޵��� �Ѵ�.
     */
    public synchronized void start() throws IOException {
        if (listener == null) {
            // ������ ip�� port�� �о� �鿩 ������ �����Ͽ� ������ �����Ѵ�.
            Socket socket = new Socket(host, port);
            try {
                // �������κ��� �Է½�Ʈ���� ȹ���Ѵ�.
                dataIn = new DataInputStream(new BufferedInputStream(socket
                        .getInputStream()));
                // �������κ��� ��½�Ʈ���� ����Ѵ�.
                dataOut = new DataOutputStream(new BufferedOutputStream(socket
                        .getOutputStream()));
                // �������� �˸��� ���� id�� �����Ѵ�.
                dataOut.writeUTF("##name##" + id);
                // ���ۿ� �����ִ� ������ ��� ��� �����ϰ� �Ѵ�.
                dataOut.flush();
            } catch (IOException ie) {
                socket.close();
                throw ie;
            }
            // listener �����带 �����ϰ� �����带 �۵��Ѵ�.
            listener = new Thread(this);
            listener.start();
        }
    }

    /**
     * �޼ҵ� : stop() 
     * listener �������� �����߿� ���ͷ�Ʈ�� �ɰ� ��Ʈ��ũ ������ �ݴ´�.
     */
    public synchronized void stop() throws IOException {
        if (listener != null) {
            listener.interrupt();
            listener = null;
            dataOut.close();
        }
    }

    /**
     * �޼ҵ� : run() 
     * listener �����尡 run() �޼ҵ� ������ �����Ͽ� ������ ���鼭 
     * ��Ʈ�����κ��� String ��ü�� �о��. String�� �д� ������ readUTF()�� ����ϴµ�
     * �������� writeUTF()�� ����ؼ� �����߱� �����̴�. 
     * ������ String�� ��� ��Ʈ���� ��� ���̸鼭 ������ ������.
     * ������ �� �����忡 ���ͷ�Ʈ�� �ɸ����� IOException ���ܰ� �߻��� �� ������. 
     * ���� �߻��� ������ ó���� ���ؼ� handleIOException()�� ȣ���Ѵ�.
     */
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // �Է½�Ʈ�����κ��� �޽����� �о�´�.
                String line = dataIn.readUTF();
                // textArea1�� �о�� �޽����� �߰��Ѵ�.
                textArea1.append(line + "\n");
            }
        } catch (IOException ie) {
            handleIOException(ie);
        } finally {
            // ����Ǿ��ִ��� ���¸� false�� �����Ѵ�.
            connectok = false;
        }
    }

    /**
     * �޼ҵ� : handleIOException() 
     * IOException ���ܸ� ó���� �� ȣ���.
     */
    protected synchronized void handleIOException(IOException ex) {
        if (listener != null) {
            // textArea1�� ���� �޽����� �߰��Ѵ�.
            textArea1.append(ex + "\n");
            // frame�� ���� ������Ʈ�� ������ ����Ǿ� ������Ʈ�� ���ġ�Ѵ�.
            frame.validate();
            if (listener != Thread.currentThread())
                listener.interrupt();
            listener = null;
            try {
                dataOut.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected boolean connectok = false;

    /**
     * �޼ҵ� : actionPerformed()
     * ��� ��Ʈ���� ������� �޽����� ����ϰ� flush()�� ȣ���Ͽ� ���۸� ��������μ� 
     * ���ۿ� �ִ� �޽����� ��ٷ� ���޽��״�. ��½�Ʈ���� Ÿ���� DataOutputStream�̱� 
     * ������ writeUTF()�� ����Ͽ� ���� �����ڵ� ���ڿ��� String ��ü�� ���� �� �ִ�. 
     * IOException ���ܰ� �߻��� ��� handleIOException() �޼ҵ�� ó��.
     */
    public void actionPerformed(ActionEvent event) {
        try {
            // ����Ǿ��ִ� ���� �ʰ� �̺�Ʈ �ҽ��� Ŀ�ǵ尡 Connect�̸� ����ȴ�.
            if (!connectok && event.getActionCommand().equals("Connect")) {
             
                // host �ּҸ� �о�� �����Ѵ�.
                host = textField1.getText();
                // port ��ȣ�� �о� �鿩 �����Ѵ�.(���ڿ��� ���ڷ� ��ȯ)
                port = Integer.parseInt(textField2.getText());
                // id�� �о� �鿩 �����Ѵ�.
                id = textField3.getText();
                // textArea1�� ������ �߰��Ѵ�.
                textArea1.append("press Connect button\n");
                // start() �޼ҵ带 ȣ���Ͽ� ȣ��Ʈ�� �����ϰ� 
                //��Ʈ���� ȹ���� �� �����带 ���۽�Ų��.
                start();

                // ���� �� �޽��� �Է� �ؽ�Ʈ�ʵ带 Ȱ��ȭ ��Ų��.
                textField4.setEditable(true);
                textField4.setText("");
                // ���¸� �����ᡱ ���·� �����Ѵ�.
                connectok = true;
            //����Ǿ��ְ� Ŀ�ǵ尡 �޽����Է� �ؽ�Ʈ�ʵ�� ��ġ�ϸ� ����ȴ�.
            } else if (connectok
                    && event.getActionCommand().equals(textField4.getText())) {
                // �޽��� �Է� �ؽ�Ʈ �ʵ��� ��� ������ �����Ѵ�.
                textField4.selectAll();
                // ��½�Ʈ���� �������� id�� �ؽ�Ʈ �ʵ��� ������ ����Ѵ�.
                dataOut.writeUTF("<" + id + "> : " + event.getActionCommand());
                // ���۸� ��� ��� ���۵ǰ� �Ѵ�.
                dataOut.flush();
                // �Է� �ؽ�Ʈ �ʵ��� ������ �ʱ�ȭ�Ѵ�.
                textField4.setText("");
            // ����Ǿ��ְ� Ŀ�ǵ尡 Disconnect�̸� ����ȴ�.
            } else if (connectok
                    && event.getActionCommand().equals("Disconnect")) {
                // textArea1�� ������ �߰��Ѵ�.
                textArea1.append("press Disconnect button\n");
                // ���������� ���� stop() �޼ҵ带 ȣ���Ѵ�.
                stop();
                // ������¸� �������� ���·� �����Ѵ�.
                connectok = false;
            }
        } catch (IOException ex) {
            handleIOException(ex);
        }
    }

    /**
     * method : main() 
     * ChatClient�� ��ü�� �����Ѵ�.
     */
    public static void main(String args[]) {
        ChatClient chat = new ChatClient();
    }
}