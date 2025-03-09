package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;

import static xyz.cliserkad.consortium.Main.JAIL_TURNS;

public class Player implements Serializable {

	@Serial
	private static final long serialVersionUID = 20240805L;

	public static final String[] PLAYER_ICONS = { "🎩", "🐈", "💰", "👽" };

	private static int numPlayers = 0;

	private int remainingJailTurns;
	private int money;
	private boolean isBankrupt;
	public final int playerIndex;
	private BoardPosition position;
	public transient final GameClient controller;

	public Player(GameClient controller) {
		position = BoardPosition.GO;
		money = 800;
		playerIndex = numPlayers++;
		this.controller = controller;
		isBankrupt = false;
		remainingJailTurns = 0;
	}

	public BoardPosition getPosition() {
		return position;
	}

	/**
	 * Sets the player's position on the board and updates the board elements
	 */
	public void setPosition(BoardPosition position, GameState gameState) {
		this.position = position;
	}

	public void transferMoney(Player recipient, int amount) {
		money -= amount;
		recipient.addMoney(amount);
		// System.out.println(getIcon() + " paid " + recipient.getIcon() + " $" + amount);
	}

	public int getMoney() {
		return money;
	}

	public void addMoney(int amount) {
		money += amount;
	}

	public String getIcon() {
		return PLAYER_ICONS[playerIndex];
	}

	@Override
	public String toString() {
		return "Player " + playerIndex + getIcon();
	}

	public void goBankrupt() {
		this.isBankrupt = true;
	}

	public boolean isBankrupt() {
		return isBankrupt;
	}

	/**
	 * Sets the remaining jail sentence to the maximum
	 */
	public void newJailSentence() {
		remainingJailTurns = JAIL_TURNS;
	}

	/**
	 * Reduces the remaining jail sentence by one turn
	 */
	public void reduceJailSentence() {
		remainingJailTurns--;
	}

	public void endJailSentence() {
		remainingJailTurns = 0;
	}

	/**
	 * Returns the remaining jail sentence
	 */
	public int getRemainingJailTurns() {
		return remainingJailTurns;
	}

}
