package xyz.cliserkad.consortium;

public class IncomeTaxLogic implements PositionLogic {

	public static final IncomeTaxLogic INSTANCE = new IncomeTaxLogic();

	public static final float INCOME_TAX_FACTOR = 0.25f;

	private IncomeTaxLogic() {

	}

	@Override
	public String onLand(Player mover, GameState gameState) {
		int deducted = (int) (mover.getMoney() * INCOME_TAX_FACTOR);
		mover.addMoney(-deducted);
		return mover.getIcon() + " paid $" + deducted + " in Income Tax";
	}

}
