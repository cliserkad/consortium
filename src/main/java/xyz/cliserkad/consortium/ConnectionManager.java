package xyz.cliserkad.consortium;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

public class ConnectionManager extends Thread {

	private final ServerSocket serverSocket;
	private final CopyOnWriteArrayList<SocketInvocationHandler> noIDConnections;
	private final ConcurrentHashMap<UUID, SocketInvocationHandler> connections;

	public ConnectionManager(int port) throws IOException {
		System.out.println("ConnectionManager started on port: " + port);
		serverSocket = new ServerSocket(port);
		noIDConnections = new CopyOnWriteArrayList<>();
		connections = new ConcurrentHashMap<>();
	}

	@Override
	public void run() {
		synchronized(serverSocket) {
			while(!serverSocket.isClosed()) {
				try {
					Socket socket = serverSocket.accept();
					SocketInvocationHandler handler = new SocketInvocationHandler(new ObjectSocket(socket));
					UUID sessionID = handler.newProxy(ConnectionMaintenance.class).getSessionID();
					if(sessionID == null) {
						synchronized(noIDConnections) {
							noIDConnections.add(handler);
							noIDConnections.notifyAll();
						}
					} else {
						synchronized(connections) {
							// Store the handler in the map with the session ID
							connections.put(sessionID, handler);
							connections.notifyAll();
						}
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Future<SocketInvocationHandler> acceptSocket() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				do {
					if(noIDConnections.isEmpty()) {
						synchronized(noIDConnections) {
							noIDConnections.wait();
						}
					} else {
						synchronized(noIDConnections) {
							return noIDConnections.removeFirst();
						}
					}
				} while(true);
			} catch(InterruptedException e) {
				return null;
			}
		});
	}

	public Future<SocketInvocationHandler> acceptSocket(UUID sessionID) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				do {
					if(connections.containsKey(sessionID)) {
						synchronized(connections) {
							return connections.remove(sessionID);
						}
					} else {
						synchronized(connections) {
							connections.wait();
						}
					}
				} while(true);
			} catch(InterruptedException e) {
				return null;
			}
		});
	}

}
