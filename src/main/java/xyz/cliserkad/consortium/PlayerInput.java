package xyz.cliserkad.consortium;

public final class PlayerInput {
	private final Player executor;
	private final EndTurnAction action;

	protected PlayerInput(final Player executor, final EndTurnAction action) {
		this.executor = executor;
		this.action = action;
	}

	public Player getExecutor() {
		return executor;
	}

	public EndTurnAction getAction() {
		return action;
	}

}
