package xyz.cliserkad.consortium;

import static xyz.cliserkad.consortium.GraphicalGameClient.MIN_BID;

/**
 * AI controller for a player.
 */
public class AutoGameClient implements GameClient {

	// The bot will bid up to 80% of the property's market value
	public static final double MAX_BID_RATIO = 0.75;

	@Override
	public PlayerAction poll(Player avatar, GameState gameState, Class<? extends PlayerAction> prompt) {
		if(prompt == PurchaseAction.class) {
			// always try to purchase property
			return new PurchaseAction(avatar.getPosition());
		} else if(prompt == BidAction.class) {
			// bid up the property if the other players are broke
			if(!canOthersWinBid(gameState, avatar))
				return new BidAction(gameState.getAuction().property.position, gameState.getAuction().bid + MIN_BID);
			// bid up the property if the bid is less than bid ratio * market value
			else if(bidLessThanMax(gameState))
				return new BidAction(gameState.getAuction().property.position, gameState.getAuction().bid + MIN_BID);
			else
				return null;
		} else if(prompt == EndTurnAction.class) {
			return new EndTurnAction();
		} else if(prompt == DeclareBankruptcyAction.class) {
			return new DeclareBankruptcyAction();
		} else if(prompt == AcceptTradeAction.class) {
			return new AcceptTradeAction(gameState.getProposedTrade(), false);
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
	public boolean canOthersWinBid(GameState gameState, Player avatar) {
		for(Player p : gameState.getPlayers()) {
			if(p != avatar) {
				if(gameState.getAuction().property.position.logic instanceof Purchasable purchasable) {
					if(p.getMoney() > purchasable.cost()) {
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
