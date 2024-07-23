package xyz.cliserkad.consortium;

import java.io.Serializable;

public class Player implements Serializable {

	private static final long serialVersionUID = 20240615L;

	public static final String[] PLAYER_ICONS = { "🎩", "🐈", "💰", "👽" };

	private static int numPlayers = 0;

	private int money;
	private boolean isBankrupt;
	public final int playerIndex;
	private BoardPosition position;
	public transient final GameClient controller;

	public Player(GameClient controller) {
		position = BoardPosition.GO;
		money = 1800;
		playerIndex = numPlayers++;
		this.controller = controller;
		isBankrupt = false;
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

}
