package xyz.cliserkad.consortium;

public interface GameStateReceiver {

	/**
	 * Update with the current game state.
	 * 
	 * @return true for success, false for failure
	 */
	boolean update(final GameState gameState);

}
