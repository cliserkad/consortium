package xyz.cliserkad.consortium;

public interface GameClient extends GameStateReceiver {

	/**
	 * Poll this client for input.
	 * The client implementation could be a local player, an AI, or a network player.
	 */
	PlayerAction poll(final Player avatar, final GameState gameState, final Class<? extends PlayerAction>[] prompts);

	void sendMessage(final String message);

}
