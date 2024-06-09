package xyz.cliserkad.consortium;

public class GoLogic implements PositionLogic {
	public static final int PASS_GO_REWARD = 200;

	@Override
	public void onLand(Player mover, Main main) {
		mover.addMoney(PASS_GO_REWARD);
	}

	@Override
	public void onPass(Player mover, Main main) {
		mover.addMoney(PASS_GO_REWARD);
	}

}
