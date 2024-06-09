package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_X_SIZE_LESS_ONE = BOARD_X_SIZE - 1;
	public static final int BOARD_Y_SIZE = 11;
	public static final int BOARD_Y_SIZE_LESS_ONE = BOARD_Y_SIZE - 1;
	public static final int DICE_MAX = 12;
	public static final int DICE_MIN = 2;
	public static final String WINDOW_TITLE = "Consortium";

	public static final Random RANDOM = new Random();

	private final JFrame frame;

	private Player[] players;
	private BoardElement[] boardElements;
	private int lastRoll = 0;
	private int currentPlayer = 0;

	public Main(final int playerCount) {
		frame = new JFrame(WINDOW_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920, 1080);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;

		boardElements = new BoardElement[BoardPosition.values().length];
		for(int i = 0; i < BoardPosition.values().length; i++) {
			final BoardPosition position = BoardPosition.values()[i];
			constraints.gridx = position.getBoardCoords().x;
			constraints.gridy = position.getBoardCoords().y;
			final BoardElement boardElement = new BoardElement(position);
			boardElements[i] = boardElement;
			panel.add(boardElement, constraints);
		}

		players = new Player[playerCount];
		constraints.gridy = 5;
		for(int i = 0; i < playerCount; i++) {
			players[i] = new Player();
			constraints.gridx = 3 + i;
			panel.add(players[i], constraints);
			players[i].setPosition(BoardPosition.GO, this);
		}

		JButton button = new JButton("CLICK TO ADVANCE");
		button.addActionListener(actionEvent -> {
			movePlayer(getCurrentPlayer(), rollDice());
			endTurn();
		});
		constraints.gridy = 6;
		constraints.gridx = 3;
		panel.add(button, constraints);

		frame.add(panel);
		frame.setVisible(true);

	}

	public void purchasingLogic(Player player, BoardElement element) {
		if(element.position.logic instanceof Purchasable purchasable && element.owner == null) {
			if(player.getMoney() >= purchasable.cost()) {
				final int dialogResult = JOptionPane.showConfirmDialog(frame, "Would you like to purchase " + element.position.name() + " for $" + purchasable.cost() + "?", "Purchase Property", JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION) {
					player.addMoney(-purchasable.cost());
					element.setOwner(player);
				}
			}
		}
	}

	public void movePlayer(Player player, int spaces) {
		for(int stepsTaken = 1; stepsTaken < spaces; stepsTaken++) {
			BoardPosition position = player.getPosition().next(stepsTaken);
			BoardElement element = getBoardElement(position);
			element.position.logic.onPass(player, this);
		}
		player.setPosition(player.getPosition().next(spaces), this);
		player.getPosition().logic.onLand(player, this);
		purchasingLogic(player, getBoardElement(player));
	}

	public void endTurn() {
		currentPlayer = (currentPlayer + 1) % players.length;
	}

	public boolean playerOwesRent(Player player) {
		BoardElement element = getBoardElement(player);
		return element.owner != null && element.owner != player && element.improvementAmt >= 0;
	}

	public Player getCurrentPlayer() {
		return players[currentPlayer];
	}

	public BoardElement getBoardElement(Player player) {
		return getBoardElement(player.getPosition());
	}

	public BoardElement getBoardElement(BoardPosition position) {
		return boardElements[position.ordinal()];
	}

	public int rollDice() {
		final int out =  RANDOM.nextInt(DICE_MAX - 1) + DICE_MIN;
		lastRoll = out;
		return out;
	}

	public int getLastRoll() {
		return lastRoll;
	}

	public BoardElement[] getBoardElements() {
		return boardElements;
	}

	public static void main(String[] args) throws InterruptedException {
		new Main(4);
	}

}