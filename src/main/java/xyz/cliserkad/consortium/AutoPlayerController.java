package xyz.cliserkad.consortium;

/**
 * AI controller for a player.
 */
public class AutoPlayerController implements PlayerController {

	@Override
	public PlayerAction poll(Player avatar, GameState gameState, Class<? extends PlayerAction>[] prompts) {
		if(avatar.getPosition().logic instanceof Purchasable) {
			return new PurchaseAction(avatar.getPosition());
		}
		return null;
	}

}
