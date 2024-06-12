package xyz.cliserkad.consortium;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static xyz.cliserkad.consortium.Main.*;
import static xyz.cliserkad.consortium.PositionLogic.EMPTY_STRING;

/**
 * Represents the state of the game, and contains logic for editing that state
 */
public class GameState {
	private final int[] communityCardStack = genShuffledArray(CommunityChestLogic.CommunityCard.values().length);
	private final int[] chanceCardStack = genShuffledArray(ChanceLogic.ChanceCard.values().length);
	private int communityCardIndex = 0;
	private int chanceCardIndex = 0;
	private Player[] players;
	private BoardElement[] boardElements;
	private int lastRoll = 0;
	private int currentPlayer = 0;

	public GameState(final int playerCount) {
		boardElements = new BoardElement[BoardPosition.values().length];
		for(int i = 0; i < BoardPosition.values().length; i++) {
			final BoardPosition position = BoardPosition.values()[i];
			final BoardElement boardElement = new BoardElement(position);
			boardElements[i] = boardElement;
		}

		players = new Player[playerCount];
		for(int i = 0; i < playerCount; i++) {
			if(i == 0)
				players[i] = new Player(new GraphicalPlayerController());
			else
				players[i] = new Player(new AutoPlayerController());
			players[i].setPosition(BoardPosition.GO, this);
		}
	}

	public void nextTurn() {
		movePlayer(getCurrentPlayer(), rollDice());
		endTurn();
	}

	public int nextCommunityCard() {
		return communityCardStack[communityCardIndex++ % communityCardStack.length];
	}

	public int nextChanceCard() {
		return chanceCardStack[chanceCardIndex++ % chanceCardStack.length];
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

}
