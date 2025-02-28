package xyz.cliserkad.consortium;

public class TaxLogic implements PositionLogic {

	public static final int LUXURY_TAX_AMOUNT = 100;
	public static final int INCOME_TAX_AMOUNT = 200;

	public static final TaxLogic LUXURY_TAX = new TaxLogic(LUXURY_TAX_AMOUNT);
	public static final TaxLogic INCOME_TAX = new TaxLogic(INCOME_TAX_AMOUNT);

	public final int amount;

	public TaxLogic(final int amount) {
		this.amount = -amount;
	}

	@Override
	public String onLand(Player mover, GameState gameState) {
		mover.addMoney(amount);
		return mover.getIcon() + " paid $" + -amount + " in taxes";
	}

}
