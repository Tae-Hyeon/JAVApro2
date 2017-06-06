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
 * Runnable 인터페이스와 ActionListener 인터페이스를 implements해서 
 * 쓰레드를 구현하고 이벤트를 핸들링한다.
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
     * 생성자 : ChatClient() 
     * 기본적인 화면 구성을 한다.
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

        //프레임에 WindowListener를 추가하여 윈도우 종료 이벤트를 생성한다.
        //WindowListener 인터페이스의 모든 내용을 implements하지 않고 필요한
        //메소드만을 사용하기 위해 WindowAdapter을 이용한다.
        frame.addWindowListener(new WindowAdapter() {
            /**
             * method : windowCloseing() 
             * 활성화 되어있지 않은 닫기 버튼을 활성화 시켜준다.
             */
            public void windowClosing(WindowEvent e) {
                try {
                    stop();
                } catch (IOException ex) {
                }
                // 프레임의 자원을 반납하고 시스템을 종료한다.
                frame.dispose();
                System.exit(0);
            }
        });
        
        //새로운 윈도우가 생성될 때 포커스처리를 위해 리스너를 추가한다.
        frame.addWindowListener(new WindowAdapter() {
            /**
             * method : windowOpened() 프레임이 뜨면 자동적으로 label1에 포커스를 맞춘다.
             */
            public void windowOpended(WindowEvent e) {
                label1.requestFocus();
            }
        });

        // panel1의 레이아웃을 4행 2열의 GridLayout으로 설정
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
        // 버튼이 눌렸을 경우 이벤트 처리를 위한 리스너를 버튼에 추가한다.
        button1.addActionListener(this);
        panel1.add(button1);

        button2.setLabel("Disconnect");
        button2.addActionListener(this);
        panel1.add(button2);

        textField4.setText("input");
        textField4.setColumns(40);
        textField4.setEditable(false);
        textField4.addActionListener(this);
        
        // panel1을 BorderLayout인 frame의 NORTH에 추가한다.
        frame.add(panel1, BorderLayout.NORTH);

        panel2.setLayout(new BorderLayout());

        panel2.add(textArea1, BorderLayout.CENTER);

        panel2.add(textField4, BorderLayout.SOUTH);

        frame.add(panel2, BorderLayout.CENTER);
        // frame을 자신의 내부 컴포넌트의 사이즈에 맞게 조정한다.
        frame.pack();
        // frame을 화면에 보이게한다.
        frame.setVisible(true);
    }

    protected DataInputStream dataIn;
    protected DataOutputStream dataOut;
    protected Thread listener;

    /**
     * 메소드 : start() 
     * 채팅 서버에 대해 Socket으로 연결한다. 
     * 연결될 소켓으로부터 버퍼링 스트림인 dataIn과 dataOut를 생성한다. 
     * 쓰레드 listener를 가동하여 서버로부터의 메시지를 받도록 한다.
     */
    public synchronized void start() throws IOException {
        if (listener == null) {
            // 서버의 ip와 port를 읽어 들여 소켓을 생성하여 서버에 접속한다.
            Socket socket = new Socket(host, port);
            try {
                // 소켓으로부터 입력스트림을 획득한다.
                dataIn = new DataInputStream(new BufferedInputStream(socket
                        .getInputStream()));
                // 소켓으로부터 출력스트림을 출력한다.
                dataOut = new DataOutputStream(new BufferedOutputStream(socket
                        .getOutputStream()));
                // 접속함을 알리기 위해 id를 전송한다.
                dataOut.writeUTF("##name##" + id);
                // 버퍼에 남아있는 내용을 비워 즉시 전송하게 한다.
                dataOut.flush();
            } catch (IOException ie) {
                socket.close();
                throw ie;
            }
            // listener 스레드를 생성하고 스레드를 작동한다.
            listener = new Thread(this);
            listener.start();
        }
    }

    /**
     * 메소드 : stop() 
     * listener 쓰레드의 실행중에 인터럽트를 걸고 네트워크 연결을 닫는다.
     */
    public synchronized void stop() throws IOException {
        if (listener != null) {
            listener.interrupt();
            listener = null;
            dataOut.close();
        }
    }

    /**
     * 메소드 : run() 
     * listener 쓰레드가 run() 메소드 안으로 진입하여 루프를 돌면서 
     * 스트림으로부터 String 객체를 읽어낸다. String을 읽는 데에는 readUTF()를 사용하는데
     * 서버에서 writeUTF()를 사용해서 전송했기 때문이다. 
     * 도달한 String은 출력 스트림에 계속 붙이면서 루프를 돌린다.
     * 루프는 이 쓰레드에 인터럽트가 걸리던지 IOException 예외가 발생할 때 끝난다. 
     * 예외 발생시 문제의 처리를 위해서 handleIOException()을 호출한다.
     */
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // 입력스트림으로부터 메시지를 읽어온다.
                String line = dataIn.readUTF();
                // textArea1에 읽어온 메시지를 추가한다.
                textArea1.append(line + "\n");
            }
        } catch (IOException ie) {
            handleIOException(ie);
        } finally {
            // 연결되어있는지 상태를 false로 설정한다.
            connectok = false;
        }
    }

    /**
     * 메소드 : handleIOException() 
     * IOException 예외를 처리할 때 호출됨.
     */
    protected synchronized void handleIOException(IOException ex) {
        if (listener != null) {
            // textArea1에 예외 메시지를 추가한다.
            textArea1.append(ex + "\n");
            // frame의 서브 컴포넌트의 내용이 변경되어 컴포넌트를 재배치한다.
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
     * 메소드 : actionPerformed()
     * 출력 스트림에 사용자의 메시지를 기록하고 flush()를 호출하여 버퍼를 비워줌으로서 
     * 버퍼에 있는 메시지를 곧바로 전달시켰다. 출력스트림의 타입이 DataOutputStream이기 
     * 때문에 writeUTF()를 사용하여 쉽게 유니코드 문자열을 String 객체로 보낼 수 있다. 
     * IOException 예외가 발생할 경우 handleIOException() 메소드로 처리.
     */
    public void actionPerformed(ActionEvent event) {
        try {
            // 연결되어있는 있지 않고 이벤트 소스의 커맨드가 Connect이면 실행된다.
            if (!connectok && event.getActionCommand().equals("Connect")) {
             
                // host 주소를 읽어와 저장한다.
                host = textField1.getText();
                // port 번호를 읽어 들여 저장한다.(문자열을 숫자로 변환)
                port = Integer.parseInt(textField2.getText());
                // id를 읽어 들여 저장한다.
                id = textField3.getText();
                // textArea1에 내용을 추가한다.
                textArea1.append("press Connect button\n");
                // start() 메소드를 호출하여 호스트에 연결하고 
                //스트림을 획득한 후 스레드를 동작시킨다.
                start();

                // 접속 후 메시지 입력 텍스트필드를 활성화 시킨다.
                textField4.setEditable(true);
                textField4.setText("");
                // 상태를 “연결” 상태로 설정한다.
                connectok = true;
            //연결되어있고 커맨드가 메시지입력 텍스트필드와 일치하면 실행된다.
            } else if (connectok
                    && event.getActionCommand().equals(textField4.getText())) {
                // 메시지 입력 텍스트 필드의 모든 내용을 선택한다.
                textField4.selectAll();
                // 출력스트림에 전송자의 id와 텍스트 필드의 내용을 출력한다.
                dataOut.writeUTF("<" + id + "> : " + event.getActionCommand());
                // 버퍼를 비워 즉시 전송되게 한다.
                dataOut.flush();
                // 입력 텍스트 필드의 내용을 초기화한다.
                textField4.setText("");
            // 연결되어있고 커맨드가 Disconnect이면 실행된다.
            } else if (connectok
                    && event.getActionCommand().equals("Disconnect")) {
                // textArea1에 내용을 추가한다.
                textArea1.append("press Disconnect button\n");
                // 전속해제를 위해 stop() 메소드를 호출한다.
                stop();
                // 연결상태를 “해제” 상태로 설정한다.
                connectok = false;
            }
        } catch (IOException ex) {
            handleIOException(ex);
        }
    }

    /**
     * method : main() 
     * ChatClient의 객체를 생성한다.
     */
    public static void main(String args[]) {
        ChatClient chat = new ChatClient();
    }
}