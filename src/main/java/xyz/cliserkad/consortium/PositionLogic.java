package xyz.cliserkad.consortium;

public interface PositionLogic {

	public static final String EMPTY_STRING = "";

	default String onLand(Player mover, GameState gameState) {
		return EMPTY_STRING;
	}

	default String onPass(Player mover, GameState gameState) {
		return EMPTY_STRING;
	}

	default boolean isPurchasable() {
		return this instanceof Purchasable;
	}

}
