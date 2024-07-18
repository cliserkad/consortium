package xyz.cliserkad.consortium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Holds game state, manages the game loop, sends updates to clients and processes client input.
 */
public class GameServer extends TimerTask {

	public static final int BASE_PORT = 5555;

	private GameState gameState;

	private int turns;

	public GameServer(final int playerCount) throws IOException, InterruptedException {
		List<NetworkedController<GameClient>> controllers = new ArrayList<>();

		for(int i = 0; i < playerCount; i++) {
			NetworkedController<GameClient> controller = new NetworkedController<>(BASE_PORT + i, GameClient.class, true);
			controllers.add(controller);

		}

		for(NetworkedController<GameClient> controller : controllers) {
			controller.start();
		}

		for(NetworkedController<GameClient> controller : controllers) {
			controller.join();
		}

		System.out.println("Connections accepted...");

		List<Player> players = new ArrayList<>();
		for(NetworkedController<GameClient> controller : controllers) {
			players.add(new Player(controller.proxy));
		}
		gameState = new GameState(players.toArray(new Player[0]));

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
		Timer timer = new Timer("GameServerTimer");
		timer.scheduleAtFixedRate(server, 6000, 6000);
	}

	@Override
	public void run() {
		if(turns > 0) {
			gameState.nextTurn();
		} else {
			System.out.println("Game Loop on Thread " + Thread.currentThread().getId());
			gameState.updatePlayers();
		}
		turns++;
	}

}
