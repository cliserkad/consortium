package xyz.cliserkad.consortium;

public class NoLogic implements PositionLogic {
	public static final NoLogic INSTANCE = new NoLogic();

	@Override
	public void onLand(Player mover, Main main) {
	}

	@Override
	public void onPass(Player mover, Main main) {
	}

}
