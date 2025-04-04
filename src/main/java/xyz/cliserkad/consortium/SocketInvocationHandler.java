package xyz.cliserkad.consortium;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.Objects;

public class SocketInvocationHandler implements InvocationHandler {

	private final ObjectSocket socket;

	public SocketInvocationHandler(final ObjectSocket socket) {
		Objects.requireNonNull(socket);
		this.socket = socket;
	}

	public InetAddress remoteAddress() {
		return socket.socket.getInetAddress();
	}

	public <ProxyType> ProxyType newProxy(Class<ProxyType> proxyClass) {
		return (ProxyType) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { proxyClass }, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return invoke0(proxy, method, args);
	}

	public Object invoke0(Object proxy, Method method, Object[] args) throws IOException, ClassNotFoundException {
		final MethodInvocation invocation = new MethodInvocation(method, args);
		synchronized(socket) {
			socket.out.reset();
			socket.out.writeObject(invocation);
			return socket.in.readObject();
		}
	}

}
