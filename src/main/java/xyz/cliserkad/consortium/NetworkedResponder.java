package xyz.cliserkad.consortium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A client which responds to method invocations made by a NetworkedController, sending the results back over the network. The InterfaceInstance should be the same class used to create the corresponding server side NetworkedController.
 */
public class NetworkedResponder<InterfaceInstance> extends Thread {

	public static final boolean DEFAULT_IS_VERBOSE = false;

	private final InterfaceInstance interfaceInstance;
	public final String address;
	public final int port;

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private List<Object> arguments;
	public boolean isVerbose;

	public NetworkedResponder(InterfaceInstance interfaceInstance, final String ip, final int port, final boolean isVerbose) throws IOException {
		this.interfaceInstance = interfaceInstance;
		this.address = ip;
		this.port = port;
		this.arguments = new ArrayList<>();
		this.isVerbose = isVerbose;
	}

	public NetworkedResponder(InterfaceInstance interfaceInstance, final String ip, final int port) throws IOException {
		this(interfaceInstance, ip, port, DEFAULT_IS_VERBOSE);
	}

	@Override
	public void start() {
		// TODO: Make this loop more robust
		if(isVerbose)
			System.out.println("Networked responder started on thread " + Thread.currentThread().threadId());
		try {
			this.socket = new Socket(address, port);
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			if(isVerbose)
				System.out.println("CLIENT Connected to server at " + socket.getRemoteSocketAddress());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
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

				List<Class<?>> parameterTypes = new ArrayList<>();
				for(Object argument : arguments) {
					parameterTypes.add(argument.getClass());
				}

				final Method targetMethod;
				try {
					targetMethod = interfaceInstance.getClass().getMethod(cmd.methodName, parameterTypes.toArray(new Class<?>[] {}));
				} catch(NoSuchMethodException e) {
					panic(e);
					return;
				}

				try {
					out.writeObject(targetMethod.invoke(interfaceInstance, arguments.toArray()));
				} catch(IllegalAccessException | IOException | InvocationTargetException e) {
					panic(e);
					return;
				}

				arguments.clear();
			} else {
				arguments.add(obj);
			}
		}
		panic(new IOException("Connection closed."));
	}

	private void panic(Exception e) {
		e.printStackTrace();
	}

}
