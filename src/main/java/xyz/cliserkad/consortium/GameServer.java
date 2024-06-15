package xyz.cliserkad.consortium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds game state, manages the game loop, sends updates to clients and processes client input.
 */
public class GameServer implements Runnable {
	private GameState gameState;
	private int turns;

	public GameServer(int playerCount, int aiCount) throws IOException, InterruptedException {
		List<Player> players = new ArrayList<>();
		List<NetworkServer> servers = new ArrayList<>();

		for(int i = 0; i < playerCount; i++) {
			NetworkServer server = new NetworkServer(5555 + i);
			server.start();
			servers.add(server);
		}

		for(int i = 0; i < aiCount; i++) {
			players.add(new Player(new AutoGameClient()));
		}

		for(NetworkServer server : servers) {
			server.join();
		}

		for(NetworkServer server : servers) {
			players.add(new Player(server));
		}

		System.err.println("Connections accepted...");
		gameState = new GameState(players.toArray(new Player[playerCount + aiCount]));
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		GameServer server = new GameServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		while(true) {
			try {
				server.run();
				Thread.sleep(2000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		if(turns > 0) {
			gameState.nextTurn();
		} else {
			System.err.println("Game Loop on Thread " + Thread.currentThread().getId());
			gameState.updatePlayers();
		}
		turns++;
	}

}
