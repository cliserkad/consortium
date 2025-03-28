package xyz.cliserkad.consortium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A client which responds to method invocations made by a NetworkedController, sending the results back over the network.
 * The InterfaceInstance should be the same class used to create the corresponding server side NetworkedController.
 */
public class NetworkedResponder<InterfaceInstance> extends Thread implements ConnectionMaintenance {

	public static final boolean DEFAULT_IS_VERBOSE = false;

	private final InterfaceInstance interfaceInstance;
	public final String address;
	public final int port;

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private List<Object> arguments;
	public boolean isVerbose;
	private ConnectionPolicy connectionPolicy;

	public NetworkedResponder(InterfaceInstance interfaceInstance, final String ip, final int port, final boolean isVerbose) throws IOException {
		this.interfaceInstance = interfaceInstance;
		this.address = ip;
		this.port = port;
		this.arguments = new ArrayList<>();
		this.isVerbose = isVerbose;
		// assume we need to start the connection immediately
		connectionPolicy = ConnectionPolicy.REQUIRED;

		if(isVerbose)
			System.out.println("Networked responder created for " + ip + ":" + port);
	}

	public NetworkedResponder(InterfaceInstance interfaceInstance, final String ip, final int port) throws IOException {
		this(interfaceInstance, ip, port, DEFAULT_IS_VERBOSE);
	}

	@Override
	public void start() {
		while(connectionPolicy.mayReconnect()) {
			while(!connect()) {
				try {
					if(isVerbose)
						System.out.println("Networked responder failed to connect, retrying in 5 seconds");
					Thread.sleep(5000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			dispatchLoop();
			if(isVerbose)
				System.out.println("Networked responder disconnected.");
		}
	}

	private void dispatchLoop() {
		while(socket.isConnected()) {
			final Object obj;
			try {
				obj = in.readObject();
			} catch(IOException | ClassNotFoundException e) {
				panic(e);
				return;
			}

			if(obj instanceof MethodInvocation cmd) {
				try {
					out.reset();
				} catch(IOException e) {
					panic(e);
					return;
				}

				final Object target;
				if(cmd.isMaintenance) {
					target = this;
				} else {
					target = interfaceInstance;
				}

				try {
					out.writeObject(cmd.against(target));
				} catch(IllegalAccessException | IOException | InvocationTargetException | NoSuchMethodException e) {
					panic(e);
					return;
				}
			} else {
				System.err.println("NetworkedResponder received an object that was not a MethodInvocation:\n" + obj);
			}
		}
	}

	private boolean connect() {
		if(isVerbose)
			System.out.println("Networked responder attempting to connect on thread " + Thread.currentThread().threadId());
		try {
			this.socket = new Socket(address, port);
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			if(isVerbose)
				System.out.println("CLIENT Connected to server at " + socket.getRemoteSocketAddress());
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void panic(Exception e) {
		e.printStackTrace();
	}

	@Override
	public String version(String version) {
		if(!version.equals(Version.COMMIT_ID)) {
			System.out.println("NetworkedResponder received a version mismatch:\n\t" + version + "\n\t" + Version.COMMIT_ID);
		}
		return Version.COMMIT_ID + " hahahahah";
	}

	@Override
	public void setConnectionPolicy(ConnectionPolicy policy, String rationale) {
		if(isVerbose)
			System.out.println("NetworkedResponder setting connection policy to " + policy + " with rationale: " + rationale);
		this.connectionPolicy = policy;
		if(policy == ConnectionPolicy.FORBIDDEN) {
			try {
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}
