package xyz.cliserkad.consortium;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.List;

import static xyz.cliserkad.consortium.PositionLogic.EMPTY_STRING;

public class Main implements ActionListener {
	public static final int BOARD_X_SIZE = 11;
	public static final int BOARD_X_SIZE_LESS_ONE = BOARD_X_SIZE - 1;
	public static final int BOARD_Y_SIZE = 11;
	public static final int BOARD_Y_SIZE_LESS_ONE = BOARD_Y_SIZE - 1;
	public static final int DICE_MAX = 12;
	public static final int DICE_MIN = 2;
	public static final String WINDOW_TITLE = "Consortium";
	public static final String PROMPT_TURN = "Prompt Turn";

	public static final Random RANDOM = new Random();

	private final JFrame frame;
	private final int[] communityCardStack = genShuffledArray(CommunityChestLogic.CommunityCard.values().length);
	private final int[] chanceCardStack = genShuffledArray(16);
	private int communityCardIndex = 0;
	private int chanceCardIndex = 0;



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

		constraints.gridy = 1;
		constraints.gridx = 1;
		constraints.gridwidth = 9;
		constraints.gridheight = 5;
		JTextArea printOutput = new JTextArea();
		printOutput.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
		printOutput.setEditable(false);
		System.setOut(new PrintStream(new TextAreaOutputStream(printOutput)));
		panel.add(
			new JScrollPane(
				printOutput,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		), constraints);
		constraints.gridwidth = 1;
		constraints.gridheight = 1;

		players = new Player[playerCount];
		constraints.gridy = 6;
		for(int i = 0; i < playerCount; i++) {
			if(i == 0)
				players[i] = new Player(new GraphicalPlayerController());
			else
				players[i] = new Player(new AutoPlayerController());
			constraints.gridx = 3 + i;
			panel.add(players[i], constraints);
			players[i].setPosition(BoardPosition.GO, this);
		}

		frame.add(panel);
		frame.setVisible(true);

		for(int i : communityCardStack) {
			System.out.println(i);
		}
		for(int i : chanceCardStack) {
			System.out.println(i);
		}

		// Game loop
		Timer timer = new Timer(3500, this);
		timer.setRepeats(true);
		timer.setInitialDelay(5000);
		timer.setActionCommand(PROMPT_TURN);
		timer.start();
	}

	/**
	 * in place shuffle of an array of integers
	 */
	public static int[] genShuffledArray(int length)
	{
		// Creating an array of integers
		Integer[] numbers = new Integer[length];
		for(int i = 0; i < length; i++)
			numbers[i] = i;

		// Converting the array to a list
		List<Integer> numberList = Arrays.asList(numbers);

		// Shuffling the list
		Collections.shuffle(numberList);

		// Converting the list back to an array
		Integer[] shuffledNumbers = numberList.toArray(new Integer[length]);

		int[] output = new int[length];
		for(int i = 0; i < length; i++)
			output[i] = shuffledNumbers[i];

		return output;
	}

	public int nextCommunityCard() {
		return communityCardStack[communityCardIndex++ % communityCardStack.length];
	}

	public int nextChanceCard() {
		return chanceCardStack[chanceCardIndex++ % chanceCardStack.length];
	}

	public String purchasingLogic(Player player, BoardElement element) {
		if(element.position.logic instanceof Purchasable purchasable && element.owner == null) {
			if(player.getMoney() >= purchasable.cost()) {
				PlayerAction response = player.controller.poll(player, this, new Class[]{ PurchaseAction.class });
				if(response instanceof PurchaseAction purchaseAction) {
					if(purchaseAction.position() == element.position) {
						player.addMoney(-purchasable.cost());
						element.setOwner(player);
						return player.getIcon() + " purchased " + element.position.niceName + " for $" + purchasable.cost();
					} else {
						return player.getIcon() + " chose not to purchase " + element.position.niceName + ". PlayerController may have returned an incorrect position";
					}
				}
				return player.getIcon() + " chose not to purchase " + element.position.niceName;
			}
			return player.getIcon() + " could not purchase " + element.position.niceName + " due to lack of funds";
		}
		return EMPTY_STRING;
	}

	public boolean movePlayer(Player player, int spaces) {
		return movePlayer(player, player.getPosition().next(spaces), spaces);
	}

	public boolean movePlayer(Player player, BoardPosition destination, int spaces) {
		player.setPosition(player.getPosition().next(), this);
		while(player.getPosition() != destination) {
			printIfContentful(player.getPosition().logic.onPass(player, this));
			player.setPosition(player.getPosition().next(), this);
		}
		if(spaces > 0) {
			System.out.println(player.getIcon() + " rolled a " + spaces + " and landed on " + player.getPosition().niceName);
		} else {
			System.out.println(player.getIcon() + " landed on " + player.getPosition().niceName);
		}
		printIfContentful(player.getPosition().logic.onLand(player, this));
		printIfContentful(purchasingLogic(player, getBoardElement(player)));
		return true;
	}

	public static boolean printIfContentful(String string) {
		if(string != null && !string.isEmpty() && !string.isBlank()) {
			System.out.println(string);
			return true;
		} else {
			return false;
		}
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

	public Player[] getPlayers() {
		return players;
	}

	public static void main(String[] args) throws InterruptedException {
		new Main(2);
	}

	public boolean isEntireGroupOwned(BoardPosition queryPosition) {
		BoardElement queryElement = getBoardElement(queryPosition);
		for(BoardPosition position : queryPosition.colorGroup()) {
			BoardElement element = getBoardElement(position);
			if(element.owner != queryElement.owner) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		movePlayer(getCurrentPlayer(), rollDice());
		endTurn();
	}

}