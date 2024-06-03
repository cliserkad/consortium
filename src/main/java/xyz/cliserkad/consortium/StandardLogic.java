package xyz.cliserkad.consortium;

public class StandardLogic implements PositionLogic {

	public final int cost;
	public final int costPerHouse;
	public final int[] rents;

	public StandardLogic(final int cost, final int costPerHouse, final int[] rents) {
		this.cost = cost;
		this.costPerHouse = costPerHouse;
		this.rents = rents;
	}

	@Override
	public void onLand(Player player) {

	}

	@Override
	public void onPass(Player player) {

	}

	@Override
	public boolean isOwnable() {
		return true;
	}

}
