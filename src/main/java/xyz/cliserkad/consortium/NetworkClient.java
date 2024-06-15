package xyz.cliserkad.consortium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkClient implements GameClient {
	/**
	 * The network connection to the game server
	 */
	private final Socket socket;

	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private List<Object> arguments;
	GraphicalGameClient graphicalGameClient = new GraphicalGameClient();

	public NetworkClient(final String ip, final int port) throws IOException {
		this.socket = new Socket(ip, port);
		System.err.println("CLIENT Connected to server at " + socket.getRemoteSocketAddress());
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
		this.arguments = new ArrayList<>();
	}

	public void run() {
		try {
			while(socket.isConnected()) {
				final Object obj = in.readObject();
				if(obj instanceof String cmd) {
					if(cmd.equals("poll")) {
						out.reset();
						out.writeObject(poll((Player) arguments.get(0), (GameState) arguments.get(1), (Class<? extends PlayerAction>[]) arguments.get(2)));
					} else if(cmd.equals("update")) {
						out.reset();
						out.writeObject(update((GameState) arguments.get(0)));
					} else
						graphicalGameClient.sendMessage(cmd);
					// System.err.println("CLIENT Received command from server: " + cmd);
					arguments.clear();
				} else {
					arguments.add(obj);
					// System.err.println("CLIENT Received object from server of type " + obj.getClass().getName());
					// System.err.println(obj);
				}
				// System.err.println("CLIENT Read message loop done.");
			}
			System.err.println("CLIENT Connection closed.");
		} catch (final IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PlayerAction poll(Player avatar, GameState gameState, Class<? extends PlayerAction>[] prompts) {
		return graphicalGameClient.poll(avatar, gameState, prompts);
	}

	@Override
	public void sendMessage(String message) {
		graphicalGameClient.sendMessage(message);
	}

	@Override
	public boolean update(GameState gameState) {
		return graphicalGameClient.update(gameState);
	}

	public static void main(String[] args) throws IOException {
		new NetworkClient(args[0], Integer.parseInt(args[1])).run();
	}

}
