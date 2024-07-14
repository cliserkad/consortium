package xyz.cliserkad.consortium;

/**
 * AI controller for a player.
 */
public class AutoGameClient implements GameClient {

	@Override
	public PlayerAction poll(Player avatar, GameState gameState, Class<? extends PlayerAction> prompt) {
		if(avatar.getPosition().logic instanceof Purchasable) {
			return new PurchaseAction(avatar.getPosition());
		}
		return null;
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
