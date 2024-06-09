package xyz.cliserkad.consortium;

public class RailRoadLogic implements Purchasable {
	public static final int[] RAILROAD_RENT = { 25, 50, 100, 200 };

	@Override
	public void onLand(Player mover, Main main) {
		BoardElement destination = main.getBoardElement(mover);
		if(main.playerOwesRent(mover)) {
			int railroadsOwned = 1;
			for(BoardElement element : main.getBoardElements()) {
				if(element.owner == destination.owner && element.position.logic instanceof RailRoadLogic) {
					railroadsOwned++;
				}
			}
			mover.transferMoney(destination.owner, RAILROAD_RENT[railroadsOwned - 1]);
		}
	}

	@Override
	public void onPass(Player mover, Main main) {

	}

	@Override
	public int cost() {
		return 200;
	}

}
