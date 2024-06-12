package xyz.cliserkad.consortium;

public class StandardLogic implements Purchasable {
	public static final int GROUP_OWNERSHIP_COEFFICIENT = 2;

	public final int cost;
	public final int costPerHouse;
	public final int[] rents;

	public StandardLogic(final int cost, final int costPerHouse, final int[] rents) {
		this.cost = cost;
		this.costPerHouse = costPerHouse;
		this.rents = rents;
	}

	@Override
	public String onLand(Player mover, GameState gameState) {
		BoardElement destination = gameState.getBoardElement(mover);

		final int rentAmount;
		if(destination.owner == null)
			rentAmount = 0;
		else if(destination.owner == mover)
			rentAmount = 0;
		else if(destination.improvementAmt == 0 && gameState.isEntireGroupOwned(destination.position))
			rentAmount = rents[0] * GROUP_OWNERSHIP_COEFFICIENT;
		else
			rentAmount = rents[destination.improvementAmt];

		if(gameState.playerOwesRent(mover)) {
			mover.transferMoney(destination.owner, rentAmount);
		}
		return rentPaidString(mover, rentAmount, destination);
	}

	public static String rentPaidString(Player mover, int rentAmount, BoardElement destination) {
		final String ownerString;
		if(destination.owner == null)
			ownerString = "THE BANK";
		else
			ownerString = destination.owner.getIcon();
		if(rentAmount > 0)
			return mover.getIcon() + " rented from " + ownerString + " for $" + rentAmount;
		else
			return EMPTY_STRING;
	}

	@Override
	public int cost() {
		return cost;
	}

}
