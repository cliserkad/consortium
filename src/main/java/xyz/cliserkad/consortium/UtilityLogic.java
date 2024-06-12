package xyz.cliserkad.consortium;

public class UtilityLogic implements Purchasable {
	public static final int[] FACTORS = { 4, 10 };

	@Override
	public String onLand(Player mover, GameState gameState) {
		BoardElement element = gameState.getBoardElement(mover);
		final int rentToPay;
		if(gameState.playerOwesRent(mover)) {
			final int factor;
			if(gameState.getBoardElement(BoardPosition.ELECTRIC_COMPANY).owner == gameState.getBoardElement(BoardPosition.WATER_WORKS).owner) {
				factor = FACTORS[1];
			} else {
				factor = FACTORS[0];
			}
			rentToPay = factor * gameState.getLastRoll();
			mover.transferMoney(element.owner, rentToPay);
		} else {
			rentToPay = 0;
		}
		return StandardLogic.rentPaidString(mover, rentToPay, element);
	}

	@Override
	public int cost() {
		return 100;
	}

}
