package xyz.cliserkad.consortium;

import static xyz.cliserkad.consortium.GraphicalGameClient.MIN_BID;

/**
 * AI controller for a player.
 */
public class AutoGameClient implements GameClient {

	// bot will bid up to 125% of the market value
	public static final double MAX_BID_RATIO = 1.25;

	@Override
	public PlayerAction poll(Player avatar, GameState gameState, Class<? extends PlayerAction> prompt) {
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			// do nothing
		}
		if(prompt == PurchaseAction.class) {
			if(!canOthersBuy(gameState, avatar))
				return null;
			else
				return new PurchaseAction(avatar.getPosition());
		} else if(prompt == BidAction.class) {
			// bid up the property if the bid is less than bid ratio * market value
			if(bidLessThanMax(gameState))
				return new BidAction(gameState.getAuction().property.position, gameState.getAuction().bid + MIN_BID);
			else
				return null;
		} else if(prompt == EndTurnAction.class) {
			return new EndTurnAction();
		} else if(prompt == DeclareBankruptcyAction.class) {
			return new DeclareBankruptcyAction();
		} else if(prompt == AcceptTradeAction.class) {
			// incoming value must be 2x the incoming value
			int incomingValue = gameState.getProposedTrade().moneyToAcceptor;
			for(BoardPosition position : gameState.getProposedTrade().positionsToAcceptor)
				incomingValue += position.logic instanceof Purchasable purchasable ? purchasable.cost() : 0;
			int outgoingValue = gameState.getProposedTrade().moneyToProposer;
			for(BoardPosition position : gameState.getProposedTrade().positionsToProposer)
				outgoingValue += position.logic instanceof Purchasable purchasable ? purchasable.cost() : 0;
			return new AcceptTradeAction(gameState.getProposedTrade(), incomingValue >= 2 * outgoingValue);
		} else {
			return null;
		}
	}

	public boolean bidLessThanMax(GameState gameState) {
		if(gameState.getAuction().property.position.logic instanceof Purchasable purchasable) {
			return gameState.getAuction().bid < (purchasable.cost() * MAX_BID_RATIO);
		}
		return false;
	}

	/**
	 * Return false if any other player has more money than the position's market value
	 */
	public boolean canOthersBuy(GameState gameState, Player avatar) {
		for(Player p : gameState.getPlayers()) {
			if(p != avatar) {
				if(gameState.getBoardElement(avatar).position.logic instanceof Purchasable purchasable) {
					if(p.getMoney() > purchasable.cost() - 25) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void sendMessage(String message) {
		// The ai doesn't care about messages
	}

	@Override
	public boolean update(GameState gameState) {
		// The ai doesn't care about intermittent game state, it only cares about the game state on its turn
		return true;
	}

}
