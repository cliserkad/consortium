package xyz.cliserkad.consortium;

public interface GameClient extends GameStateReceiver {

	/**
	 * Poll this client for input. The client implementation could be a local player, an AI, or a network player.
	 */
	PlayerAction poll(final Player avatar, final GameState gameState, final Class<? extends PlayerAction> prompt);

	@NetOptional
	void sendMessage(final String message);

	@NetOptional
	default void setPlayerID(final Integer playerID) {
		// do nothing
	}

	@NetOptional
	default String name() {
		return "";
	}

}
