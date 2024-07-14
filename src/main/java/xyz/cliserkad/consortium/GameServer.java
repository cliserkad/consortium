package xyz.cliserkad.consortium;

import javax.swing.*;
import java.io.IOException;
import java.util.TimerTask;
import java.util.Timer;

/**
 * Holds game state, manages the game loop, sends updates to clients and processes client input.
 */
public class GameServer extends TimerTask {
	private GameState gameState;

	private int turns;

	public GameServer(int playerCount) throws IOException, InterruptedException {
		NetworkServer server0 = new NetworkServer(0);
		NetworkServer server1 = new NetworkServer(1);
		server0.start();
		server1.start();
		server0.join();
		server1.join();
		Player p0 = new Player(server0);
		Player p1 = new Player(server1);

		System.out.println("Connections accepted...");

		Player[] players = { p0, p1 };
		gameState = new GameState(players);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		GameServer server = new GameServer(3);
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
