package IWannaEat.info;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Communication {
	private Socket socket;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public DataInputStream getDataIn() {
		return dataIn;
	}

	public void setDataIn(DataInputStream dataIn) {
		this.dataIn = dataIn;
	}

	public DataOutputStream getDataOut() {
		return dataOut;
	}

	public void setDataOut(DataOutputStream dataOut) {
		this.dataOut = dataOut;
	}
}
