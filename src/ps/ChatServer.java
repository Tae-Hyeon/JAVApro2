package ps;

/*
 *  ChatServer.java
 */

import java.io.*;
import java.net.*;

/**
 * class : ChatServer 
 * Ŭ���̾�Ʈ�� ������ ����ؼ� ��ٸ��� ���ӽ� �̸� ChatHandler�� 
 * �����ڿ� �Ѱ��ְ� ChatHandler�� ��ü�� �����Ѵ�.
 */
public class ChatServer {
    /*
     * method : main() 
     * ������ �°� ���Դ��� ������ �� (������ ����� ��Ʈ ��ȣ�� �˻��ϴ� ���̴�)
     * ServerSocket��ü�� �����ϰ� ������ �����鼭 ServerSocket�� accept() �޼ҵ带 ���� 
     * Ŭ���̾�Ʈ�κ����� ������ �޴´�. �� ���ῡ ���Ͽ� ChatHandler Ŭ������ �ν��Ͻ��� 
     * ���� ����µ� ���⿡ accept()�� ��� �� �� ������ �Ű������� ����Ѵ�. 
     * �ڵ鷯 ��ü�� ���� �Ŀ��� start() �޼ҵ�� �̰��� ���۽�Ű�� �̶� �־��� ������ 
     * ó���� �� �ִ� �����尡 ���� �����ϰ� �ȴ�. 
     * �׸��� �� ����, ���� ������ ����Ͽ� ������ ���鼭 ���ο� ������ ��ٸ��� �ȴ�.
     */
    public static void main(String args[]) throws IOException {
        if (args.length != 1)
            throw new IllegalArgumentException("Syntax: ChatServer <port>");
        int port = Integer.parseInt(args[0]);
        // �Է¹��� ��Ʈ��ȣ�� ���������� �����Ѵ�.
        ServerSocket server = new ServerSocket(port);

        System.out.println("ChatServer Started..!!!");
        //�ټ��� Ŭ���̾�Ʈ�� ������ ����ؼ� ����ϱ� ���Ͽ� �ݺ��Ѵ�.
        while (true) {
            //Ŭ���̾�Ʈ�� ������ ����Ѵ�.
            Socket client = server.accept();
            //������ Ŭ���̾�Ʈ�� �������
            System.out.println("Accepted from: " + client.getInetAddress());
            //ChatHandler�� �����ϰ� ���� ������ Ŭ���̾�Ʈ�� ���ϰ�ü�� �����Ѵ�.
            ChatHandler handler = new ChatHandler(client);
            //�ڵ鷯 ��ü�� start() �޼ҵ� ȣ��(��Ʈ�������� ������ �۵�)�Ѵ�.
            handler.start();
        }

    }
}