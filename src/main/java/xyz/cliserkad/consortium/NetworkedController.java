package xyz.cliserkad.consortium;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

/**
 * Controls the invocation of methods on a remote object by sending method calls over a network connection.
 * Available methods are defined by providing an interface class (ProxyType) to the constructor.
 */
public class NetworkedController<ProxyType> extends Thread implements InvocationHandler, ConnectionMaintenance {

	public static final boolean DEFAULT_IS_VERBOSE = false;

	public final ProxyType proxy;
	private final ConnectionMaintenance maintenance;
	public final int port;

	/**
	 * The network connection to coordinating server
	 */
	private ServerSocket serverSocket;
	private Socket clientSocket;

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ConnectionPolicy connectionPolicy;
	public boolean isVerbose;

	/**
	 * Initializes a new networked controller, against which all methods supplied by ProxyType can be invoked. The controller will listen on the specified port for incoming connections.
	 */
	public NetworkedController(final int port, final Class<ProxyType> proxy, final boolean isVerbose) throws IOException {
		this.port = port;
		this.isVerbose = isVerbose;
		connectionPolicy = ConnectionPolicy.REQUIRED;

		// trust that the standard library actually works
		this.proxy = (ProxyType) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { proxy }, this);
		maintenance = (ConnectionMaintenance) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { ConnectionMaintenance.class }, this);
	}

	public NetworkedController(final int port, final Class<ProxyType> proxy) throws IOException {
		this(port, proxy, DEFAULT_IS_VERBOSE);
	}

	@Override
	public void run() {
		run0();
	}

	private void run0() {
		try {
			if(serverSocket != null)
				serverSocket.close();
			serverSocket = new ServerSocket(port);
			if(isVerbose)
				System.out.println("Server started and listening on port " + serverSocket.getLocalPort());
			clientSocket = serverSocket.accept();

			if(isVerbose)
				System.out.println("Client connected from " + clientSocket.getRemoteSocketAddress());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());

			final String clientVersion = maintenance.version();
			if(!clientVersion.equals(Version.COMMIT_ID))
				System.err.println("Version mismatch with " + clientSocket.getRemoteSocketAddress() + "\n\tLocal : " + Version.COMMIT_ID + "\n\tClient: " + clientVersion);
			else if(isVerbose)
				System.out.println("Version match with " + clientSocket.getRemoteSocketAddress());

			setConnectionPolicy(connectionPolicy, "");
		} catch(IOException e) {
			if(isVerbose) {
				System.err.println("NetworkedController encountered an IOException. Will retry connection...");
				e.printStackTrace();
			}
			sleep();
			run0();
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return invoke0(proxy, method, args, 0);
	}

	private Object invoke0(Object proxy, Method method, Object[] args, int callNum) throws Throwable {
		if(connectionPolicy == ConnectionPolicy.FORBIDDEN)
			throw new IllegalStateException("Connection policy is set to FORBIDDEN. No method invocations are allowed.");

		if(callNum != 0)
			sleep();

		if(callNum > 3) {
			if(isVerbose)
				System.err.println("Multiple IOExceptions encountered. Attempting to restart connection...");
			run0();
		}

		try {
			out.reset();
		} catch(IOException e) {
			if(isVerbose) {
				System.err.println("NetworkedController encountered an IOException. Will retry invocation...");
				e.printStackTrace();
			}
			return invoke0(proxy, method, args, ++callNum);
		}

		final MethodInvocation invocation = new MethodInvocation(method, args, proxy == maintenance);
		try {
			out.writeObject(invocation);
		} catch(InvalidClassException invalidClassException) {
			if(isVerbose) {
				System.err.println("FATAL: NetworkedController can't write out given class.");
				invalidClassException.printStackTrace();
			}
			throw invalidClassException;
		} catch(NotSerializableException notSerializableException) {
			if(isVerbose) {
				System.err.println("FATAL: NetworkedController can't serialize given object.");
				notSerializableException.printStackTrace();
			}
			throw notSerializableException;
		} catch(IOException ioException) {
			if(isVerbose) {
				System.err.println("NetworkedController encountered an IOException. Will retry invocation...");
				ioException.printStackTrace();
			}
			return invoke0(proxy, method, args, ++callNum);
		}

		try {
			return in.readObject();
		} catch(ClassNotFoundException classNotFoundException) {
			if(isVerbose) {
				System.err.println("FATAL: NetworkedController can't load class for object received from client/NetworkedResponder");
				classNotFoundException.printStackTrace();
			}
			throw classNotFoundException;
		} catch(InvalidClassException invalidClassException) {
			if(isVerbose) {
				System.err.println("FATAL: NetworkedController can't read in class from client/NetworkedResponder.");
				invalidClassException.printStackTrace();
			}
			throw invalidClassException;
		} catch(StreamCorruptedException streamCorruptedException) {
			if(isVerbose) {
				System.err.println("NetworkedController encountered a StreamCorruptedException. Will retry invocation...");
				streamCorruptedException.printStackTrace();
			}
			return invoke0(proxy, method, args, ++callNum);
		} catch(OptionalDataException optionalDataException) {
			if(isVerbose) {
				System.err.println("FATAL: NetworkedController received primitive data types instead of objects. The client/NetworkedResponder needs to wrap primitives.");
				optionalDataException.printStackTrace();
			}
			throw optionalDataException;
		} catch(IOException ioException) {
			if(isVerbose) {
				System.err.println("NetworkedController encountered an IOException. Will retry invocation...");
				ioException.printStackTrace();
			}
			return invoke0(proxy, method, args, ++callNum);
		}
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			if(isVerbose) {
				e.printStackTrace();
				System.err.println("Sleep interrupted, continuing...");
			}
		}
	}

	@Override
	public String toString() {
		return NetworkedController.class.getName() + "\nProxy:" + proxy.getClass().getSuperclass() + "\nPort: " + port + "\nClient Address: " + clientSocket.getRemoteSocketAddress() + "\nVerbose?: " + isVerbose;
	}

	@Override
	public Instant ping() {
		return maintenance.ping();
	}

	@Override
	public String version() {
		return maintenance.version();
	}

	@Override
	public void setConnectionPolicy(ConnectionPolicy policy, String rationale) {
		if(isVerbose) {
			System.out.println("Setting connection policy to: " + policy + " with rationale: " + rationale);
		}
		connectionPolicy = policy;
		maintenance.setConnectionPolicy(policy, rationale);

		if(connectionPolicy == ConnectionPolicy.FORBIDDEN) {
			try {
				in.close();
				out.close();
				serverSocket.close();
				clientSocket.close();
			} catch(IOException e) {
				if(isVerbose) {
					System.err.println("Failed to clean up FORBIDDEN connection");
					e.printStackTrace();
				}
			}
		}
	}

}
