package xyz.cliserkad.consortium;

public interface PlayerController {

	/**
	 * Method for polling this controller for input.
	 * The controller implementation could be a local player, an AI, or a network player.
	 */
	PlayerAction poll(final Player avatar, final Main main, final Class<? extends PlayerAction>[] prompts);

}
