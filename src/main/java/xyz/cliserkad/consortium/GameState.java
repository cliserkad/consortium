package xyz.cliserkad.consortium;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import static xyz.cliserkad.consortium.Main.*;
import static xyz.cliserkad.consortium.PositionLogic.EMPTY_STRING;

/**
 * Represents the state of the game, and contains logic for editing that state
 */
public class GameState implements Serializable {

	private final transient int[] communityCardStack = genShuffledArray(CommunityChestLogic.CommunityCard.values().length);
	private final transient int[] chanceCardStack = genShuffledArray(ChanceLogic.ChanceCard.values().length);
	private int communityCardIndex = 0;
	private int chanceCardIndex = 0;
	private Player[] players;
	private BoardElement[] boardElements;
	private int lastRoll = 0;
	private int currentPlayer = 0;
	private Auction auction = null;
	/**
	 * The actions which the GameServer is currently waiting for from the current player
	 */
	private Set<Class<? extends PlayerAction>> blockingActions;

	/**
	 * Version for serialization. When editing classes, update this number to the current date in YYYYMMDD format
	 */
	@Serial
	private static final long serialVersionUID = 20240615L;

	public GameState(Player[] players) {
		boardElements = new BoardElement[BoardPosition.values().length];
		for(int i = 0; i < BoardPosition.values().length; i++) {
			final BoardPosition position = BoardPosition.values()[i];
			final BoardElement boardElement = new BoardElement(position);
			boardElements[i] = boardElement;
		}

		this.players = players;

		for(Player player : this.players) {
			player.setPosition(BoardPosition.GO, this);
		}

		blockingActions = new HashSet<>();
	}

	/**
	 * Creates a new game state with no players. Used as a placeholder for updating clients during game initialization.
	 */
	public GameState() {
		this(new Player[0]);
	}

	/**
	 * Updates all Players with this GameState
	 *
	 * @return true if all Players return true
	 */
	public boolean updatePlayers() {
		boolean output = true;
		for(Player player : players) {
			if(!player.controller.update(this))
				output = false;
		}
		return output;
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
			broadcast(player.getPosition().logic.onPass(player, this));
			player.setPosition(player.getPosition().next(), this);
		}
		if(spaces > 0) {
			broadcast(player.getIcon() + " rolled a " + spaces + " and landed on " + player.getPosition().niceName);
		} else {
			broadcast(player.getIcon() + " landed on " + player.getPosition().niceName);
		}
		broadcast(player.getPosition().logic.onLand(player, this));
		updatePlayers();
		Duo<String, Boolean> purchaseResult = purchasingLogic(player, getBoardElement(player));
		broadcast(purchaseResult.a);
		updatePlayers();
		if(getBoardElement(player).position.logic instanceof Purchasable && getBoardElement(player).owner == null && !purchaseResult.b)
			broadcast(holdAuction(player, getBoardElement(player)));
		return true;
	}

	/**
	 * Polls the current player for input
	 *
	 * @param prompt the type of action to prompt for
	 * @return the action the player chose
	 */
	private PlayerAction updateAndPoll(Player player, Class<? extends PlayerAction> prompt) {
		blockingActions.add(prompt);
		updatePlayers();
		final PlayerAction response = player.controller.poll(player, this, prompt);
		blockingActions.remove(prompt);
		updatePlayers();
		return response;
	}

	private String holdAuction(Player firstBidder, BoardElement element) {
		if(element.position.logic instanceof Purchasable purchasable) {
			// add all players who can afford the property to the auction
			auction = new Auction(element, new ArrayList<>(Arrays.asList(players)));
			auction.bidders.removeIf(player -> player.getMoney() < 1);

			// abort if no players have any money :(
			if(auction.bidders.isEmpty())
				return "No players have enough money to bid on " + element.position.niceName;

			// set the current bidder to the first bidder if they can participate
			if(auction.bidders.contains(firstBidder))
				auction.currentBidder = firstBidder;
			else
				auction.currentBidder = auction.bidders.getFirst();

			while((auction.bidders.size() == 1 && auction.bid <= 0) || auction.bidders.size() > 1) {
				PlayerAction response = updateAndPoll(auction.currentBidder, BidAction.class);
				if(response instanceof BidAction bidAction && bidAction.amount() > auction.bid) {
					auction.bid = bidAction.amount();
					auction.bids.add(bidAction);
					broadcast(auction.currentBidder.getIcon() + " bid $" + auction.bid + " on " + element.position.niceName);
				} else {
					auction.bidders.remove(auction.currentBidder);
					broadcast(auction.currentBidder.getIcon() + " withdrew from bidding on " + element.position.niceName);
				}
				if(!auction.bidders.isEmpty())
					auction.currentBidder = auction.bidders.get((auction.currentBidder.playerIndex + 1) % auction.bidders.size());
			}

			if(!auction.bidders.isEmpty() && auction.bid > 0) {
				final Player auctionWinner = auction.bidders.getFirst();
				auctionWinner.addMoney(-auction.bid);
				element.setOwner(auctionWinner);
				return auctionWinner.getIcon() + " won the auction for " + element.position.niceName + " for $" + auction.bid;
			} else {
				return "Auction for " + element.position.niceName + " failed.";
			}
		} else {
			return EMPTY_STRING;
		}
	}

	/**
	 * Handles the logic for purchasing a property
	 *
	 * @return true if a property was purchased
	 */
	public Duo<String, Boolean> purchasingLogic(Player player, BoardElement element) {
		if(element.position.logic instanceof Purchasable purchasable && element.owner == null) {
			if(player.getMoney() >= purchasable.cost()) {
				PlayerAction response = updateAndPoll(player, PurchaseAction.class);
				if(response instanceof PurchaseAction purchaseAction) {
					if(purchaseAction.position() == element.position) {
						player.addMoney(-purchasable.cost());
						element.setOwner(player);
						return new Duo<>(player.getIcon() + " purchased " + element.position.niceName + " for $" + purchasable.cost(), true);
					} else {
						return new Duo<>(player.getIcon() + " GameClient returned an incorrect position\n", false);
					}
				}
				return new Duo<>(player.getIcon() + " chose not to purchase " + element.position.niceName, false);
			}
			return new Duo<>(player.getIcon() + " could not purchase " + element.position.niceName + " due to lack of funds", false);
		}
		return new Duo<>(EMPTY_STRING, false);
	}

	public boolean broadcast(String string) {
		if(string != null && !string.isEmpty() && !string.isBlank()) {
			System.out.println(string);
			for(Player player : players) {
				player.controller.sendMessage(string);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets currentPlayer to the next player
	 */
	public void endTurn() {
		currentPlayer = (currentPlayer + 1) % players.length;
	}

	public boolean playerOwesRent(Player player) {
		BoardElement element = getBoardElement(player);
		return element.owner != null && element.owner != player && element.improvementAmt >= 0;
	}

	public Auction getAuction() {
		return auction;
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
		final int out = RANDOM.nextInt(DICE_MAX - 1) + DICE_MIN;
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

	public Set<Class<? extends PlayerAction>> getBlockingActions() {
		return blockingActions;
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
	public static int[] genShuffledArray(int length) {
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
