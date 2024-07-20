package xyz.cliserkad.consortium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds game state, manages the game loop, sends updates to clients and processes client input.
 */
public class GameServer {

	public static final int BASE_PORT = 5555;

	private GameState gameState;

	private int turns;

	public GameServer(final int playerCount) throws IOException, InterruptedException {
		List<NetworkedController<GameClient>> controllers = new ArrayList<>();

		for(int i = 0; i < playerCount; i++) {
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
		for(NetworkedController<GameClient> controller : controllers) {
			clients.add(controller.proxy);
		}
		gameState = new GameState(clients);
		System.out.println("Game state initialized...");
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		final int playerCount;
		try {
			playerCount = Integer.parseInt(args[0]);
		} catch(ArrayIndexOutOfBoundsException | NumberFormatException e) {
			System.err.println("Failed to parse player count from command line arguments");
			return;
		}
		GameServer server = new GameServer(playerCount);
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
