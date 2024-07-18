package xyz.cliserkad.consortium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Controls the invocation of methods on a remote object by sending method calls over a network connection, available methods are defined by providing an array of interfaces to the constructor
 */
public class NetworkedController<InterfaceClass> extends Thread implements InvocationHandler {

	public static final boolean DEFAULT_IS_VERBOSE = false;

	public InterfaceClass proxy;
	/**
	 * The network connection to coordinating server
	 */
	private final ServerSocket serverSocket;
	private Socket clientSocket;

	private ObjectInputStream in;
	private ObjectOutputStream out;
	public boolean isVerbose;

	/**
	 * Initializes a new networked controller, against which all methods supplied by InterfaceClass can be invoked. The controller will listen on the specified port for incoming connections.
	 */
	public NetworkedController(final int port, final Class<InterfaceClass> interfaceClass, final boolean isVerbose) throws IOException {
		serverSocket = new ServerSocket(port);
		this.isVerbose = isVerbose;

		// trust that the standard library actually works
		proxy = (InterfaceClass) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { interfaceClass }, this);
	}

	public NetworkedController(final int port, final Class<InterfaceClass> interfaceClass) throws IOException {
		this(port, interfaceClass, DEFAULT_IS_VERBOSE);
	}

	@Override
	public void start() {
		try {
			if(isVerbose)
				System.out.println("Server started and listening on port " + serverSocket.getLocalPort());
			clientSocket = serverSocket.accept();
			if(isVerbose)
				System.out.println("Client connected from " + clientSocket.getRemoteSocketAddress());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		out.reset();
		for(Object arg : args) {
			out.writeObject(arg);
		}
		out.writeObject(new MethodInvocation(method.getName()));
		return in.readObject();
	}

}
