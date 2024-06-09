package xyz.cliserkad.consortium;

public class UtilityLogic implements Purchasable {
	public static final int[] FACTORS = { 4, 10 };

	@Override
	public void onLand(Player mover, Main main) {
		BoardElement element = main.getBoardElement(mover);
		if(main.playerOwesRent(mover)) {
			final int factor;
			if(main.getBoardElement(BoardPosition.ELECTRIC_COMPANY).owner == main.getBoardElement(BoardPosition.WATER_WORKS).owner) {
				factor = FACTORS[1];
			} else {
				factor = FACTORS[0];
			}
			mover.transferMoney(element.owner, factor * main.getLastRoll());
		}
	}

	@Override
	public void onPass(Player mover, Main main) {

	}

	@Override
	public int cost() {
		return 100;
	}

}
