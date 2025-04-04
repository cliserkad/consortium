package xyz.cliserkad.consortium;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Controls the invocation of methods on a remote object by sending method calls over a network connection.
 * Available methods are defined by providing an interface class (ProxyType) to the constructor.
 */
public class NetworkedController<ProxyType> extends Thread implements InvocationHandler {

	public static final boolean DEFAULT_IS_VERBOSE = false;

	public final ProxyType proxy;
	public final UUID sessionID;

	private ConnectionManager manager;
	private SocketInvocationHandler socketHandler;
	private ConnectionMaintenance maintenance;

	private ConnectionPolicy connectionPolicy;
	public boolean isVerbose;

	/**
	 * Initializes a new networked controller, against which all methods supplied by ProxyType can be invoked. The controller will listen on the specified port for incoming connections.
	 */
	public NetworkedController(final Class<ProxyType> proxy, final boolean isVerbose, ConnectionManager manager) {
		this.isVerbose = isVerbose;
		connectionPolicy = ConnectionPolicy.REQUIRED;
		sessionID = UUID.randomUUID();
		this.manager = manager;

		// trust that the standard library actually works
		this.proxy = (ProxyType) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { proxy }, this);
	}

	public NetworkedController(final Class<ProxyType> proxy, ConnectionManager manager) {
		this(proxy, DEFAULT_IS_VERBOSE, manager);
	}

	@Override
	public void run() {
		run0();
	}

	private void run0() {
		try {
			socketHandler = manager.acceptSocket().get();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		} catch(ExecutionException e) {
			throw new RuntimeException(e);
		}
		maintenance = socketHandler.newProxy(ConnectionMaintenance.class);
		maintenance.setConnectionPolicy(connectionPolicy, "");
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return invoke0(proxy, method, args);
	}

	private Object invoke0(Object proxy, Method method, Object[] args) {
		if(connectionPolicy == ConnectionPolicy.FORBIDDEN)
			throw new IllegalStateException("Connection policy is set to FORBIDDEN. No method invocations are allowed.");

		try {
			try {
				return socketHandler.invoke0(proxy, method, args);
			} catch(IOException e) {
				try {
					socketHandler = manager.acceptSocket(sessionID).get();
				} catch(InterruptedException ex) {
					throw new RuntimeException(ex);
				} catch(ExecutionException ex) {
					throw new RuntimeException(ex);
				}
				return socketHandler.invoke0(proxy, method, args);
			}
		} catch(ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return NetworkedController.class.getName() + "\nProxy:" + proxy.getClass().getSuperclass() + "\nClient Address: " + socketHandler.remoteAddress() + "\nVerbose?: " + isVerbose;
	}

}
