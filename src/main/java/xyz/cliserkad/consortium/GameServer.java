package xyz.cliserkad.consortium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.cliserkad.util.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds game state, manages the game loop, sends updates to clients and processes client input.
 */
public class GameServer {

	public static final int BASE_PORT = 5555;

	private GameState gameState;

	private int turns;

	public GameServer() throws IOException, InterruptedException {
		System.out.println("server starting...");

		GameConfig gameConfig = readConfigFile(new GameConfig(), GameConfig.class);
		LobbyConfig lobbyConfig = readConfigFile(new LobbyConfig(), LobbyConfig.class);

		System.out.println("Public IP: " + publicIP());

		List<NetworkedController<GameClient>> controllers = new ArrayList<>();
		for(int i = 0; i < lobbyConfig.networkClients; i++) {
			controllers.add(new NetworkedController<>(BASE_PORT + i, GameClient.class, true));
		}

		for(NetworkedController<GameClient> controller : controllers) {
			controller.start();
		}

		for(NetworkedController<GameClient> controller : controllers) {
			controller.join();
		}

		System.out.println("Connections accepted...");
		List<GameClient> clients = new ArrayList<>();
		for(int i = 0; i < lobbyConfig.autoClients; i++) {
			clients.add(new AutoGameClient());
		}
		for(NetworkedController<GameClient> controller : controllers) {
			clients.add(controller.proxy);
		}

		gameState = new GameState(clients, gameConfig);
		System.out.println("Game state initialized...");
	}

	public static String publicIP() {
		try {
			URL amazonIP = new URI("http://checkip.amazonaws.com").toURL();
			BufferedReader in = new BufferedReader(new InputStreamReader(amazonIP.openStream()));
			return in.readLine();
		} catch(Exception e) {
			return null;
		}
	}

	public static <T> T readConfigFile(T config, Class<T> configClass) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileName = Text.undoCamelCase(config.getClass().getSimpleName()) + ".json";
		try {
			config = gson.fromJson(new FileReader(fileName), configClass);
		} catch(IOException e) {
			System.err.println(e.getMessage());
			System.err.println("Failed to read " + fileName);
		}
		System.out.println("Current " + config.getClass().getSimpleName() + ":");
		System.out.println(gson.toJson(config));
		return config;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		GameServer server = new GameServer();
		while(server.run()) {
		}
	}

	public boolean run() {
		if(turns > 0) {
			if(gameState.activePlayerCount() > 1) {
				gameState.nextTurn();
			} else if(gameState.activePlayerCount() == 1) {
				gameState.broadcast("Game over! " + gameState.getCurrentPlayer() + " wins!");
				return false;
			} else {
				gameState.broadcast("No Players in game. Exiting...");
				return false;
			}
		} else {
			System.out.println("Game Loop on Thread " + Thread.currentThread().threadId());
			gameState.updatePlayers();
		}
		turns++;
		return true;
	}

}
