package ps;

/*
 *  ChatServer.java
 */

import java.io.*;
import java.net.*;

/**
 * class : ChatServer 
 * 클라이언트의 접속을 계속해서 기다리며 접속시 이를 ChatHandler의 
 * 생성자에 넘겨주고 ChatHandler의 객체를 생성한다.
 */
public class ChatServer {
    /*
     * method : main() 
     * 인자의 맞게 들어왔는지 조사한 후 (서버가 실행될 포트 번호를 검사하는 것이다)
     * ServerSocket객체를 생성하고 루프에 빠지면서 ServerSocket의 accept() 메소드를 통해 
     * 클라이언트로부터의 연결을 받는다. 각 연결에 대하여 ChatHandler 클래스의 인스턴스를 
     * 새로 만드는데 여기에 accept()로 얻어 낸 새 소켓을 매개변수로 사용한다. 
     * 핸들러 객체를 만든 후에는 start() 메소드로 이것을 동작시키며 이때 주어진 연결을 
     * 처리할 수 있는 쓰레드가 새로 동작하게 된다. 
     * 그리고 그 동안, 메인 서버는 계속하여 루프를 돌면서 새로운 연결을 기다리게 된다.
     */
    public static void main(String args[]) throws IOException {
        if (args.length != 1)
            throw new IllegalArgumentException("Syntax: ChatServer <port>");
        int port = Integer.parseInt(args[0]);
        // 입력받은 포트번호로 서버소켓을 생성한다.
        ServerSocket server = new ServerSocket(port);

        System.out.println("ChatServer Started..!!!");
        //다수의 클라이언트의 연결을 계속해서 대기하기 위하여 반복한다.
        while (true) {
            //클라이언트의 접속을 대기한다.
            Socket client = server.accept();
            //접속한 클라이언트의 정보출력
            System.out.println("Accepted from: " + client.getInetAddress());
            //ChatHandler를 생성하고 현재 접속한 클라이언트의 소켓객체를 전달한다.
            ChatHandler handler = new ChatHandler(client);
            //핸들러 객체의 start() 메소드 호출(스트림생성과 스레드 작동)한다.
            handler.start();
        }

    }
}