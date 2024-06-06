package xyz.cliserkad.consortium;

public interface PositionLogic {

	void onLand(Player mover, Main main);

	void onPass(Player mover, Main main);

	default boolean isPurchasable() {
		return this instanceof Purchasable;
	}

}
