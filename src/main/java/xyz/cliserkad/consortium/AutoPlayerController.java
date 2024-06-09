package xyz.cliserkad.consortium;

import javax.swing.*;

/**
 * AI controller for a player.
 */
public class AutoPlayerController implements PlayerController {

	@Override
	public PlayerAction poll(Player avatar, Main main, Class<? extends PlayerAction>[] prompts) {
		if(avatar.getPosition().logic instanceof Purchasable) {
			return new PurchaseAction(avatar.getPosition());
		}
		return null;
	}

}
