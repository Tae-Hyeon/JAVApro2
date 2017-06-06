package ps;

/**
 * ChatHandler.java
 */

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * class : ChatHandler 
 * public class ChatHandler implements Runnable는
 * 서버로 들어온 클라이언트와의 연결을 개별적으로 처리하는 일을 맡은 Thread 클래스. 
 * 클라이언트로부터의 메시지를 받고, 연결된 클라이언트들에게 이것을 다시 보내는 역할을 한다.
 */
public class ChatHandler implements Runnable {
    protected Socket socket;

    /**
     * 생성자 : ChatHandler() 
     * 생성자는 클라이언트로부터 들어온 연결을 맡은 소켓인 
     * socket의 참조자를 받아 복사해둔다.
     */
    public ChatHandler(Socket socket) {
        this.socket = socket;
    }

    protected DataInputStream dataIn;

    protected DataOutputStream dataOut;

    protected Thread listener;

    /**
     * 메소드 : start() 
     * start()는 입력/출력 스트림을 연다. 버퍼링 데이터 스트림을 
     * 사용하여 String을 주고받을 수 있는 기능을 제공한다. 
     * 클라이언트 처리용 쓰레드인 listener를 새로 만들어 시작한다.
     */
    public synchronized void start() {
        if (listener == null) {
            try {
	      // 소켓으로부터 입력스트림을 획득한다.
                dataIn = new DataInputStream(new BufferedInputStream(socket
                        .getInputStream()));
                // 소켓으로부터 출력스트림을 획득한다.
                dataOut = new DataOutputStream(new BufferedOutputStream(socket
                        .getOutputStream()));
	      //listener 스레드를 생성하고 시작.
                listener = new Thread(this);
                listener.start();
            } catch (IOException ignored) {
            }
        }

    }

    /**
     * 메소드 : stop() 
     * listener 쓰레드에 인터럽트를 걸고 dataOut 스트림을 닫는다.
     */
    public synchronized void stop() {
        if (listener != null) {
            try {
                if (listener != Thread.currentThread())
                    listener.interrupt();
                listener = null;
                dataOut.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected static Vector<ChatHandler> handlers = new Vector<ChatHandler>();

    protected String myname;

    /**
     * 메소드 : run() 
     * 쓰레드의 실행이 가장 먼저 시작되는 부분. 만들어진 쓰레드를 
     * ChatHandler 객체를 넣는 벡터인 handlers에 추가한다. 
     * 이 벡터는 현재 동작중인 모든 핸들러(쓰레드)를 리스트로 유지한다.
     */
    public void run() {
        try {
            // 전체 클라이언트 목록관리를 위해 클라이언트 핸들러 객체를 등록
            handlers.addElement(this);

            while (!Thread.interrupted()) {
                // 입력스트림으로부터 메시지를 읽는다.
                // 블록킹 되어있으므로 입력내용을 대기한다.
                String message = dataIn.readUTF();
                //사용자의 입장을 위한 특수 문장확인을 위해 메시지의 시작 내용을 조사한다.
                if (message.startsWith("##name##")) {
                    //메시지를 잘라내 ##name##을 제외한 사용자 이름만을 사용한다.
                    myname = message.substring(8);
                    message = myname + "님이 입장하셨습니다.";
                }
                // 모든 클라이언트에게 메시지를 전송한다.
                broadcast(message);
            }
        } catch (EOFException ignored) {
        } catch (IOException ie) {
            if (listener == Thread.currentThread())
                ie.printStackTrace();
        } finally {

            // 퇴장을 처리하기 위해 클라이언트 목록에서 자신을 제거한다.
            handlers.removeElement(this);
            // 퇴장메시지를 모든 클라이언트에게 전송한다.
            broadcast(myname + "님이 퇴장하셨습니다.");
        }
        stop();
    }

    /**
     * 메소드 : broadcast() 
     * 모든 클라이언트에게 메시지를 전달하는 메소드. 
     * 우선 핸들러 리스트에 대해서 동기화한다.
     * (synchronized(handlers) {...}) 루프를 돌고 있는 동안에는 다른 클라이언트가 
     * 끼어들거나 현재 연결된 클라이언트가 접속을 해제하면 안되기 때문이다. 
     * 일단 현재 동작중인 핸들러의 Enumeration 객체를 얻어낸다. 
     * Vector에 든 모든 요소를 하나씩 처리하는데 편리하기 때문이다. 
     * 루프를 돌면서 Enumeration 객체 내의 모든 요소(핸들러)에다가 메시지를 쓴다. 
     * (결과적으로 연결해 있는 채팅 클라이언트에 메시지를 보내는 것이다.)
     */
    protected void broadcast(String message) {
        // 동기화블럭을 이용해서 동기화처리한다.
        synchronized (handlers) {
            Enumeration<ChatHandler> e = handlers.elements();
            // 클라이언트의 수만큼 반복한다.
            while (e.hasMoreElements()) {
                // 반복중에 클라이언트 핸들러 객체를 얻어온다.
                ChatHandler handler = e.nextElement();
                try {
                    // 핸들러객체의 출력스트림에 메시지를 전송한다.
                    handler.dataOut.writeUTF(message);
                    // 버퍼에 남아있는 내용을 비워 즉시 전송되게한다.
                    handler.dataOut.flush();
                } catch (IOException ex) {
                    handler.stop();
                }
            }
        }
    }
}