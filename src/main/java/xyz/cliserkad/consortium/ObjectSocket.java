package xyz.cliserkad.consortium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObjectSocket {

	public final Socket socket;
	public final ObjectOutputStream out;
	public final ObjectInputStream in;

	public ObjectSocket(Socket socket) throws IOException {
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}

}
