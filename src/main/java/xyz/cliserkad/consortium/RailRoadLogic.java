package xyz.cliserkad.consortium;

public class RailRoadLogic implements Purchasable {
	public static final int[] RAILROAD_RENT = { 0, 25, 50, 100, 200 };
	public static final int RAILROAD_COST = 200;

	@Override
	public String onLand(Player mover, Main main) {
		BoardElement destination = main.getBoardElement(mover);
		int railroadsOwned = 0;
		if(main.playerOwesRent(mover)) {
			for(BoardElement element : main.getBoardElements()) {
				if(element.owner == destination.owner && element.position.logic instanceof RailRoadLogic) {
					railroadsOwned++;
				}
			}
			mover.transferMoney(destination.owner, RAILROAD_RENT[railroadsOwned]);
		}
		return StandardLogic.rentPaidString(mover, RAILROAD_RENT[railroadsOwned], destination);
	}

	@Override
	public int cost() {
		return RAILROAD_COST;
	}

}
