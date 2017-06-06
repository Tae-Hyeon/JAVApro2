package ps;

/**
 * ChatHandler.java
 */

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * class : ChatHandler 
 * public class ChatHandler implements Runnable��
 * ������ ���� Ŭ���̾�Ʈ���� ������ ���������� ó���ϴ� ���� ���� Thread Ŭ����. 
 * Ŭ���̾�Ʈ�κ����� �޽����� �ް�, ����� Ŭ���̾�Ʈ�鿡�� �̰��� �ٽ� ������ ������ �Ѵ�.
 */
public class ChatHandler implements Runnable {
    protected Socket socket;

    /**
     * ������ : ChatHandler() 
     * �����ڴ� Ŭ���̾�Ʈ�κ��� ���� ������ ���� ������ 
     * socket�� �����ڸ� �޾� �����صд�.
     */
    public ChatHandler(Socket socket) {
        this.socket = socket;
    }

    protected DataInputStream dataIn;

    protected DataOutputStream dataOut;

    protected Thread listener;

    /**
     * �޼ҵ� : start() 
     * start()�� �Է�/��� ��Ʈ���� ����. ���۸� ������ ��Ʈ���� 
     * ����Ͽ� String�� �ְ���� �� �ִ� ����� �����Ѵ�. 
     * Ŭ���̾�Ʈ ó���� �������� listener�� ���� ����� �����Ѵ�.
     */
    public synchronized void start() {
        if (listener == null) {
            try {
	      // �������κ��� �Է½�Ʈ���� ȹ���Ѵ�.
                dataIn = new DataInputStream(new BufferedInputStream(socket
                        .getInputStream()));
                // �������κ��� ��½�Ʈ���� ȹ���Ѵ�.
                dataOut = new DataOutputStream(new BufferedOutputStream(socket
                        .getOutputStream()));
	      //listener �����带 �����ϰ� ����.
                listener = new Thread(this);
                listener.start();
            } catch (IOException ignored) {
            }
        }

    }

    /**
     * �޼ҵ� : stop() 
     * listener �����忡 ���ͷ�Ʈ�� �ɰ� dataOut ��Ʈ���� �ݴ´�.
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
     * �޼ҵ� : run() 
     * �������� ������ ���� ���� ���۵Ǵ� �κ�. ������� �����带 
     * ChatHandler ��ü�� �ִ� ������ handlers�� �߰��Ѵ�. 
     * �� ���ʹ� ���� �������� ��� �ڵ鷯(������)�� ����Ʈ�� �����Ѵ�.
     */
    public void run() {
        try {
            // ��ü Ŭ���̾�Ʈ ��ϰ����� ���� Ŭ���̾�Ʈ �ڵ鷯 ��ü�� ���
            handlers.addElement(this);

            while (!Thread.interrupted()) {
                // �Է½�Ʈ�����κ��� �޽����� �д´�.
                // ���ŷ �Ǿ������Ƿ� �Է³����� ����Ѵ�.
                String message = dataIn.readUTF();
                //������� ������ ���� Ư�� ����Ȯ���� ���� �޽����� ���� ������ �����Ѵ�.
                if (message.startsWith("##name##")) {
                    //�޽����� �߶� ##name##�� ������ ����� �̸����� ����Ѵ�.
                    myname = message.substring(8);
                    message = myname + "���� �����ϼ̽��ϴ�.";
                }
                // ��� Ŭ���̾�Ʈ���� �޽����� �����Ѵ�.
                broadcast(message);
            }
        } catch (EOFException ignored) {
        } catch (IOException ie) {
            if (listener == Thread.currentThread())
                ie.printStackTrace();
        } finally {

            // ������ ó���ϱ� ���� Ŭ���̾�Ʈ ��Ͽ��� �ڽ��� �����Ѵ�.
            handlers.removeElement(this);
            // ����޽����� ��� Ŭ���̾�Ʈ���� �����Ѵ�.
            broadcast(myname + "���� �����ϼ̽��ϴ�.");
        }
        stop();
    }

    /**
     * �޼ҵ� : broadcast() 
     * ��� Ŭ���̾�Ʈ���� �޽����� �����ϴ� �޼ҵ�. 
     * �켱 �ڵ鷯 ����Ʈ�� ���ؼ� ����ȭ�Ѵ�.
     * (synchronized(handlers) {...}) ������ ���� �ִ� ���ȿ��� �ٸ� Ŭ���̾�Ʈ�� 
     * �����ų� ���� ����� Ŭ���̾�Ʈ�� ������ �����ϸ� �ȵǱ� �����̴�. 
     * �ϴ� ���� �������� �ڵ鷯�� Enumeration ��ü�� ����. 
     * Vector�� �� ��� ��Ҹ� �ϳ��� ó���ϴµ� ���ϱ� �����̴�. 
     * ������ ���鼭 Enumeration ��ü ���� ��� ���(�ڵ鷯)���ٰ� �޽����� ����. 
     * (��������� ������ �ִ� ä�� Ŭ���̾�Ʈ�� �޽����� ������ ���̴�.)
     */
    protected void broadcast(String message) {
        // ����ȭ���� �̿��ؼ� ����ȭó���Ѵ�.
        synchronized (handlers) {
            Enumeration<ChatHandler> e = handlers.elements();
            // Ŭ���̾�Ʈ�� ����ŭ �ݺ��Ѵ�.
            while (e.hasMoreElements()) {
                // �ݺ��߿� Ŭ���̾�Ʈ �ڵ鷯 ��ü�� ���´�.
                ChatHandler handler = e.nextElement();
                try {
                    // �ڵ鷯��ü�� ��½�Ʈ���� �޽����� �����Ѵ�.
                    handler.dataOut.writeUTF(message);
                    // ���ۿ� �����ִ� ������ ��� ��� ���۵ǰ��Ѵ�.
                    handler.dataOut.flush();
                } catch (IOException ex) {
                    handler.stop();
                }
            }
        }
    }
}