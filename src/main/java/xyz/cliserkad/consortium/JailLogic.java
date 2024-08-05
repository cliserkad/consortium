package xyz.cliserkad.consortium;

public class JailLogic implements PositionLogic {

	@Override
	public String onLand(Player mover, GameState gameState) {
		mover.newJailSentence();
		mover.setPosition(BoardPosition.JAIL, gameState);
		return mover.getIcon() + " is in jail";
	}

}
