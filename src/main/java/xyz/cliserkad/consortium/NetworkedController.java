package xyz.cliserkad.consortium;

import java.io.*;
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

	public final InterfaceClass proxy;
	public final int port;

	/**
	 * The network connection to coordinating server
	 */
	private ServerSocket serverSocket;
	private Socket clientSocket;

	private ObjectInputStream in;
	private ObjectOutputStream out;
	public boolean isVerbose;

	/**
	 * Initializes a new networked controller, against which all methods supplied by InterfaceClass can be invoked. The controller will listen on the specified port for incoming connections.
	 */
	public NetworkedController(final int port, final Class<InterfaceClass> interfaceClass, final boolean isVerbose) throws IOException {
		this.port = port;
		this.isVerbose = isVerbose;

		// trust that the standard library actually works
		proxy = (InterfaceClass) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { interfaceClass }, this);
	}

	public NetworkedController(final int port, final Class<InterfaceClass> interfaceClass) throws IOException {
		this(port, interfaceClass, DEFAULT_IS_VERBOSE);
	}

	@Override
	public void start() {
		start0();
	}

	private void start0() {
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
		} catch(IOException e) {
			if(isVerbose) {
				System.err.println("NetworkedController encountered an IOException. Will retry connection...");
				e.printStackTrace();
			}
			sleep();
			start0();
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return invoke0(proxy, method, args, 0);
	}

	private Object invoke0(Object proxy, Method method, Object[] args, int callNum) throws Throwable {
		if(callNum != 0)
			sleep();

		if(callNum > 3) {
			if(isVerbose)
				System.err.println("Multiple IOExceptions encountered. Attempting to restart connection...");
			start0();
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

		try {
			for(Object arg : args) {
				out.writeObject(arg);
			}
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
			out.writeObject(new MethodInvocation(method.getName()));
		} catch(InvalidClassException | NotSerializableException invalidClassException) {
			if(isVerbose) {
				System.err.println("FATAL: NetworkedController can't serialize the MethodInvocation object.");
				invalidClassException.printStackTrace();
			}
			throw invalidClassException;
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

}
