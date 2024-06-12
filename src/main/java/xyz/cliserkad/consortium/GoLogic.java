package xyz.cliserkad.consortium;

public class GoLogic implements PositionLogic {
	public static final int PASS_GO_REWARD = 200;

	@Override
	public String onLand(Player mover, GameState gameState) {
		mover.addMoney(PASS_GO_REWARD);
		return mover.getIcon() + " collected $" + PASS_GO_REWARD + ".";
	}

	@Override
	public String onPass(Player mover, GameState gameState) {
		mover.addMoney(PASS_GO_REWARD);
		return mover.getIcon() + " passed Go and collected $" + PASS_GO_REWARD + ".";
	}

}
