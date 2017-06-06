package ps;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class CustomerHandler {
	String Wshap;
	String Wid;
	String Slist [];
	DataInputStream dataIn;
	DataOutputStream dataOut;
	ObjectInputStream oin;
	Socket Csocket;
	
	String Infoname;
	String Infoid;
	String shapname;
	String id;
	int table;
	int bt;
	public CustomerHandler(String line, Socket socket) throws Exception{
		
		Csocket = socket;
		StringTokenizer check = new StringTokenizer(line,"|");
		System.out.println(line);
		check.nextToken();
		Wid = check.nextToken();
		Wshap = check.nextToken();
		
		String path ="./up/"+Wshap;
		
		dataIn = new DataInputStream(new FileInputStream(path));
		String save = dataIn.readUTF();
		StringTokenizer sInfo = new StringTokenizer(save,"|");
		Infoname = sInfo.nextToken();
		Infoid = sInfo.nextToken();
		dataIn.close();
		System.out.println(Infoid);
		String path2 = "./idoption/"+Infoid+".txt";
		oin = new ObjectInputStream(new FileInputStream(path2));
		Option o= (Option)oin.readObject();
		id= o.getid();
		shapname = o.getShapname();
		table = o.gettalbe();
		bt = o.getbreakime();
		
		String pass = "가게기본정보"+"|"+id+"|"+shapname+"|"+table+"|"+bt;
		System.out.println(pass);
		dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		dataOut.writeUTF(pass);
		dataOut.flush();
	}

}
