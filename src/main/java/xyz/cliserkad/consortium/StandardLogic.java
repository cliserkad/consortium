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

		final String ownerString;
		if(element.owner == null)
			ownerString = "THE BANK";
		else
			ownerString = element.owner.getIcon();

		final int rentAmount;
		if(element.owner == null)
			rentAmount = 0;
		else if(element.owner == mover)
			rentAmount = 0;
		else
			rentAmount = rents[element.improvementAmt];

		System.out.println(mover.getIcon() + " owes " + ownerString + " $" + rentAmount + " for landing on " + element.position.name() + ".");

		if(main.playerOwesRent(mover)) {
			mover.transferMoney(element.owner, rents[element.improvementAmt]);
		}
	}

	@Override
	public void onPass(Player mover, Main main) {

	}

	@Override
	public int cost() {
		return cost;
	}

}
