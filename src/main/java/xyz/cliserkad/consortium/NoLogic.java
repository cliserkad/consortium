package xyz.cliserkad.consortium;

public class NoLogic implements PositionLogic {
	public static final NoLogic INSTANCE = new NoLogic();

	@Override
	public void onLand(Player player) {
	}

	@Override
	public void onPass(Player player) {
	}

	@Override
	public boolean isOwnable() {
		return false;
	}
}
