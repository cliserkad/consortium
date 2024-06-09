package xyz.cliserkad.consortium;

public class StandardLogic implements Purchasable {

	public final int cost;
	public final int costPerHouse;
	public final int[] rents;

	public StandardLogic(final int cost, final int costPerHouse, final int[] rents) {
		this.cost = cost;
		this.costPerHouse = costPerHouse;
		this.rents = rents;
	}

	@Override
	public void onLand(Player mover, Main main) {
		BoardElement element = main.getBoardElement(mover);
		if(main.playerOwesRent(mover))
			mover.transferMoney(element.owner, rents[element.improvementAmt]);
		else
			System.out.println("You don't owe rent.");
	}

	@Override
	public void onPass(Player mover, Main main) {

	}

	@Override
	public int cost() {
		return cost;
	}

}
