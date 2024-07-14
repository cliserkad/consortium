package xyz.cliserkad.consortium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class NetworkServer extends Thread implements GameClient {
	public static final int BASE_PORT = 5555;

	private ServerSocket socket;
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private final int playerID;


	public NetworkServer(int playerID) throws IOException {
		this.socket = new ServerSocket(BASE_PORT + playerID);
		this.playerID = playerID;
	}

	@Override
	public void sendMessage(String message) {
		try {
			out.reset();
			out.writeObject(message);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		try {
			System.out.println("SERVER Server started and listening on port " + socket.getLocalPort());
			final Socket clientSocket = socket.accept();
			System.out.println("SERVER Client connected from " + clientSocket.getRemoteSocketAddress());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			update(new GameState(new Player[0]));
			sendMessage("Player ID: " + playerID);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PlayerAction poll(Player avatar, GameState gameState, Class<? extends PlayerAction>[] prompts) {
		try {
			out.reset();
			out.writeObject(avatar);
			out.writeObject(gameState);
			out.writeObject(prompts);
			out.writeObject("poll");
			final Object obj = in.readObject();
			if(obj instanceof PlayerAction playerAction) {
				// System.err.println("SERVER client responded with action " + playerAction.getClass().getName());
				return playerAction;
			} else {
				if(obj == null)
					System.err.println("SERVER client responded with null");
				else {
					System.err.println("SERVER Recieved invalid object from client of type " + obj.getClass().getName());
					System.err.print("SERVER ");
					System.err.println(obj);
				}
				return null;
			}
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean update(GameState gameState) {
		try {
			// System.err.println("SERVER Sending game state to network client");
			out.reset();
			out.writeObject(gameState);
			out.writeObject("update");
			final Object status = in.readObject();
			if(status instanceof Boolean) {
				// System.err.println("SERVER client acknowledged update success? " + status);
				return (Boolean) status;
			} else {
				System.err.println("SERVER Recieved invalid object from client of type " + status.getClass().getName());
				System.err.println(status);
				return false;
			}
		} catch (final IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

}
