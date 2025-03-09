package xyz.cliserkad.consortium;

public class LuxuryTaxLogic implements PositionLogic {

	public static final LuxuryTaxLogic INSTANCE = new LuxuryTaxLogic();

	public static final int LUXURY_TAX_AMOUNT = 50;

	private LuxuryTaxLogic() {

	}

	@Override
	public String onLand(Player mover, GameState gameState) {
		mover.addMoney(-LUXURY_TAX_AMOUNT);
		return mover.getIcon() + " paid $" + LUXURY_TAX_AMOUNT + " in Luxury Tax";
	}

}
